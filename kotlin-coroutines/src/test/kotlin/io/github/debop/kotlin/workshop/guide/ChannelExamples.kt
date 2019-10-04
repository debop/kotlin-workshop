package io.github.debop.kotlin.workshop.guide

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class ChannelExamples {

    companion object: KLogging()

    @Test
    fun `send data and receive by channel`() {
        val channel = Channel<Int>()
        runBlocking {
            launch {
                (1..5).forEach { channel.send(it * it) }
            }

            repeat(5) {
                val received = channel.receive()
                logger.debug { "Received: $received" }
            }
            logger.debug { "Done!" }
        }
    }

    @Test
    fun `explicit close channel`() = runBlocking<Unit> {
        val channel = Channel<Int>()

        launch {
            (1..5).forEach { channel.send(it * it) }
            channel.close()
        }

        for (y in channel) {
            logger.debug { "received: $y" }
        }
        logger.debug { "Done!" }
    }


    @Test
    fun `consume each from ReceiveChannel`() {

        fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce(capacity = 3) {
            (1..5).forEach { send(it * it) }
        }
        runBlocking {
            val squares = produceSquares()
            squares.consumeEach {
                logger.debug { "received: $it" }
            }
            logger.debug { "Done!" }
        }
    }
}