package io.github.debop.rsocket.server

import io.github.debop.rsocket.api.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import mu.KLogging
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.messaging.rsocket.retrieveFlux
import org.springframework.stereotype.Controller
import java.time.Duration
import javax.annotation.PreDestroy

@Controller
class RSocketController {

    companion object: KLogging() {
        const val SERVER = "Server"
        const val RESPONSE = "Response"
        const val STREAM = "Stream"
        const val CHANNEL = "Channel"
    }

    private val clients = mutableListOf<RSocketRequester>()

    @PreDestroy
    fun shutdown() {
        logger.info { "Detaching all remaining clients ..." }
        clients.forEach { requester ->
            runCatching { requester.rsocket()?.dispose() }
        }
        logger.info { "Shutting down." }
    }

    /**
     * Shell client 가 접속했을 때, 접속되었음을 알려준다.
     * client 에서는 접속 후에 주기적으로 free memory를 서버로 전송한다.
     *
     * @param requester
     * @param client
     */
    @ConnectMapping("shell-client")
    fun connectShellClientAndAskForTelemetry(requester: RSocketRequester, @Payload client: String) {
        requester.rsocket()!!
            .onClose()
            .doFirst {
                // Add all new clients to a client list
                logger.info { "Client: $client CONNECTED." }
                clients.add(requester)
            }
            .doOnEach { }
            .doFinally { }
            .subscribe()

        // Callback to client, confirming connection
        requester.route("client-status")
            .data("OPEN")
            .retrieveFlux<String>()
            .doOnNext { logger.info { "Client: $client Free Memory: $it" } }
            .subscribe()
    }

    @MessageMapping("request-response")
    suspend fun requestResponse(request: Message): Message {
        logger.debug { "Received request-response request: $request" }
        return Message(SERVER, RESPONSE)
    }

    @MessageMapping("fire-and-forget")
    suspend fun fireAndForget(request: Message) {
        logger.debug { "Received fire-and-forget request: $request" }
    }

    @ExperimentalCoroutinesApi
    @MessageMapping("stream")
    suspend fun stream(request: Message): Flow<Message> {
        logger.info { "Received stream request: $request" }

        return flow {
            var index = 0L
            while (true) {
                val message = Message(SERVER, STREAM, index++)
                logger.info { "Send stream response: $message" }
                emit(message)
            }
        }
            .onEach { delay(1000L) }
    }

    @ExperimentalCoroutinesApi
    @MessageMapping("channel")
    suspend fun channel(settings: Flow<Duration>): Flow<Message> {
        logger.info { "Received channel request ... " }

        return settings
            .onEach { logger.info { "Channel frequency setting is ${it.seconds}" } }
            .onCompletion { logger.warn("The client cancelled the channel.") }
            .flatMapLatest { setting ->
                flow {
                    var index = 0L
                    while (true) {
                        emit(Message(SERVER, CHANNEL, index++))
                    }
                }.onEach { delay(setting.toMillis()) }
            }
    }
}