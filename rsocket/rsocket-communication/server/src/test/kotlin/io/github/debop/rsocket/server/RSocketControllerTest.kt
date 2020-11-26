package io.github.debop.rsocket.server

import io.github.debop.rsocket.api.Message
import io.rsocket.SocketAcceptor
import io.rsocket.core.RSocketServer
import io.rsocket.frame.decoder.PayloadDecoder
import io.rsocket.metadata.WellKnownMimeType
import io.rsocket.transport.netty.server.CloseableChannel
import io.rsocket.transport.netty.server.TcpServerTransport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.cbor.Jackson2CborDecoder
import org.springframework.http.codec.cbor.Jackson2CborEncoder
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.messaging.rsocket.retrieveAndAwaitOrNull
import org.springframework.messaging.rsocket.retrieveFlow
import org.springframework.util.MimeTypeUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

class RSocketControllerTest {

    companion object: KLogging()

    class ClientHandler {

        companion object: KLogging()

        @MessageMapping("client-status")
        fun statusUpdate(status: String): Flow<String> {
            logger.info { "Connection: $status" }
            return flow {
                delay(5000)
                emit(Runtime.getRuntime().freeMemory().toString())
            }
        }
    }

    @Configuration
    class ServerConfig {
        @Bean
        fun controller(): RSocketController = RSocketController()

        @Bean
        fun messageHandler(): RSocketMessageHandler = RSocketMessageHandler().apply {
            rSocketStrategies = rsocketStrategies()
        }

        @Bean
        fun rsocketStrategies(): RSocketStrategies =
            RSocketStrategies.builder()
                .encoder(Jackson2CborEncoder())
                .decoder(Jackson2CborDecoder())
                .build()
    }

    // 자제적인 Bean 설정을 가지고 테스트를 수행합니다.
    private val metadataMimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING.string)
    private val context = AnnotationConfigApplicationContext(ServerConfig::class.java)
    private val messageHandler = context.getBean<RSocketMessageHandler>()
    private val responder: SocketAcceptor = messageHandler.responder()

    private val server: CloseableChannel =
        RSocketServer.create(responder)
            .payloadDecoder(PayloadDecoder.ZERO_COPY)
            .bind(TcpServerTransport.create("localhost", 7001))
            .block()!!

    private val requester: RSocketRequester =
        RSocketRequester.builder()
            .metadataMimeType(metadataMimeType)
            .rsocketStrategies(context.getBean<RSocketStrategies>())
            .tcp("localhost", 7001)

    @AfterAll
    fun cleanup() {
        requester.rsocket()?.dispose()
        server.dispose()
    }

    @Test
    fun `fire and forget`() = runBlocking<Unit> {
        val result = requester.route("fire-and-forget")
            .data(Message("TEST", "Fire-And-Forget"))
            .retrieveAndAwaitOrNull<Unit>()
    }

    @Test
    fun `request get response`() = runBlocking<Unit> {
        val result = requester.route("request-response")
            .data(Message("TEST", "Request"))
            .retrieveAndAwait<Message>()

        result shouldBeEqualTo Message(RSocketController.SERVER, RSocketController.RESPONSE, 0)
    }

    @Test
    fun `request get stream`() = runBlocking<Unit> {
        val result = requester.route("stream")
            .data(Message("TEST", "Stream"))
            .retrieveFlow<Message>()

        result.take(5).collectIndexed { i, m ->
            m shouldBeEqualTo Message(RSocketController.SERVER, RSocketController.STREAM, i.toLong())
        }
    }

    @Test
    fun `channel - stream get stream`() = runBlocking<Unit> {
        val setting1 = Mono.just(Duration.ofSeconds(6)).delayElement(Duration.ofSeconds(0))
        val setting2 = Mono.just(Duration.ofSeconds(6)).delayElement(Duration.ofSeconds(9))
        val settings = Flux.concat(setting1, setting2).asFlow()

        val result = requester.route("channel")
            .data(settings)
            .retrieveFlow<Message>()

        try {
            result.take(2).collect {
                it shouldBeEqualTo Message(RSocketController.SERVER, RSocketController.CHANNEL, 0L)
            }
        } catch (e: Exception) {
            logger.error(e) { "Canceled." }
        }
    }


}