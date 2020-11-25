package io.github.debop.rsocket.server

import io.github.debop.rsocket.api.Message
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.rsocket.context.LocalRSocketServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler
import org.springframework.messaging.rsocket.connectTcpAndAwait
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.messaging.rsocket.retrieveAndAwaitOrNull
import java.util.UUID


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RSocketServerApplicationTest(
    @Autowired private val builder: RSocketRequester.Builder,
    @Autowired private val strategies: RSocketStrategies,
    @LocalRSocketServerPort private val port: Int,
) {

    val responder = RSocketMessageHandler.responder(strategies, ClientHandler())

    val requester = runBlocking {
        builder
            .setupRoute("shell-client")
            .setupData(UUID.randomUUID().toString())
            .rsocketConnector { it.acceptor(responder) }
            .connectTcpAndAwait("localhost", port)
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
}