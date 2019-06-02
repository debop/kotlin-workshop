package io.github.debop.kotlin.workshop.examples.basic.step2

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.Test

/**
 * ChannelExamples
 * @author debop (Sunghyouk Bae)
 */
class ChannelExamples {

    companion object : KLogging()

    @Test
    fun `send data via channel`() = runBlocking {
        val channel = Channel<Int>()
        launch {
            repeat(5) {
                logger.debug { "Send $it" }
                channel.send(it)
            }
        }

        repeat(5) {
            val received = channel.receive()
            logger.debug { "Received $received" }
        }
        logger.debug { "Done!" }
    }
}