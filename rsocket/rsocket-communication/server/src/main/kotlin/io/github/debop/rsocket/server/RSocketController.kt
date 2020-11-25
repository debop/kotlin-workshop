package io.github.debop.rsocket.server

import io.github.debop.rsocket.api.Message
import mu.KLogging
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Controller
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

    @MessageMapping("request-response")
    suspend fun requestResponse(request: Message): Message {
        logger.debug { "Received request-response request: $request" }
        return Message(SERVER, RESPONSE)
    }

    @MessageMapping("fire-and-forget")
    suspend fun fireAndForget(request: Message) {
        logger.debug { "Received fire-and-forget request: $request" }
    }
}