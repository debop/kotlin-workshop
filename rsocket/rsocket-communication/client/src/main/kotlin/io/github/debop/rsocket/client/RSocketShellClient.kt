package io.github.debop.rsocket.client

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.RSocketStrategies
import org.springframework.shell.standard.ShellComponent

@ShellComponent
class RSocketShellClient(
    @Autowired private val builder: RSocketRequester.Builder,
    @Qualifier("rSocketStrategies") private val strategies: RSocketStrategies,
) {

    companion object: KLogging() {
        const val CLIENT = "Client"
        const val REQUEST = "Request"
        const val FIRE_AND_FORGET = "Fire-And-Forget"
        const val STREAM = "Stream"
    }
}

class ClientHandler {

    companion object: KLogging()

    fun statusUpdate(status: String): Flow<String> {
        logger.info { "Connection $status" }
        return flow {
            while (true) {
                emit(Runtime.getRuntime().freeMemory().toString())
            }
        }.onEach { delay(5000L) }
    }
}