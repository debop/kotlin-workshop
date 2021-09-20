package io.github.debop.kotlin.workshop.guide

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class ChannelExamples: CoroutineScope by CoroutineScope(CoroutineName("channel") + Dispatchers.IO) {

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

    @Test
    fun `chained producer`() {

        fun CoroutineScope.produceNumbers() = produce<Int> {
            var x = 1
            while (true) {
                logger.debug { "Send $x" }
                send(x++)
            }
        }

        fun CoroutineScope.square(numbers: ReceiveChannel<Int>) = produce {
            for (x in numbers) {
                logger.debug { "Receive $x, send ${x * x}" }
                send(x * x)
            }
        }

        runBlocking {
            val numbers = produceNumbers()
            val squares = square(numbers)

            (1..5).forEach {
                val received = squares.receive()
                logger.debug { "Received: $received" }
            }
            logger.debug { "Done!" }

            // numbers, squares는 독자적인 coroutine context에서 무한대로 produce 하기 때문에 이렇게 취소해줘야 한다
            coroutineContext.cancelChildren()
        }
    }

    @Test
    fun `chained producer with seed and filter`() {
        fun CoroutineScope.numbersFrom(start: Int) = produce<Int> {
            var x = start
            while (true) {
                logger.debug { "Send $x" }
                send(x++)
            }
        }

        fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
            for (x in numbers) {
                if (x % prime != 0) {
                    logger.debug { "x=$x, prime=$prime" }
                    send(x)
                }
            }
        }

        runBlocking {
            var cur = numbersFrom(2)
            for (i in 1..10) {
                val prime = cur.receive()
                logger.debug { "prime=$prime" }
                cur = filter(cur, prime)
            }

            coroutineContext.cancelChildren()
        }
    }

    @Test
    fun `launch process from channel`() {

        fun CoroutineScope.produceNumbers() = produce<Int> {
            var x = 1
            while (true) {
                logger.debug { "Send $x" }
                send(x++)
                delay(100)
            }
        }

        fun CoroutineScope.launchProcessor(id: Int, channel: ReceiveChannel<Int>): Job {
            val job = launch {
                for (msg in channel) {
                    logger.debug { "Processor #$id received $msg" }
                    delay(200)
                }
            }
            job.invokeOnCompletion {
                logger.debug { "processor #$id is completed." }
            }
            return job
        }

        runBlocking {
            val producer = produceNumbers()
            repeat(5) {
                // 5개의 worker coroutines가 생성되고, 독립적으로 작업을 수행합니다.
                launchProcessor(it, producer)
            }
            delay(2000)
            producer.cancel()  // producer를 취소하면, producer 뿐 아니라 이를 통해 실행된 launcher들도 취소됩니다.
            logger.debug { "Done!" }
        }
    }

    @Test
    fun `send and receive string with channel`() {
        suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
            while (true) {
                delay(time)
                logger.debug { "Send $s" }
                channel.send(s)
            }
        }

        runBlocking {
            val channel = Channel<String>()
            launch { sendString(channel, "foo", 200L) }
            launch { sendString(channel, "BAR!", 500L) }

            repeat(6) {
                val received = channel.receive()
                logger.debug { received }
            }

            coroutineContext.cancelChildren()
        }
    }

    @Test
    fun `send and receive string with buffered channel`() {
        runBlocking {
            val channel = Channel<Int>(4)

            val sender = launch {
                repeat(10) {
                    logger.debug { "Sending $it" }
                    // channel buffer가 4이므로, 5개만 send되고, 더 이상은 send 하지 않는다.
                    channel.send(it)
                }
            }
            // 아무 것도 받지 않고, 기다리기만 한다.
            delay(1000)
            sender.cancel()
        }
    }

    @Test
    fun `ping pong with channel`() {

        data class Ball(var hits: Int)

        suspend fun player(name: String, table: Channel<Ball>) {
            for (ball in table) {
                ball.hits++
                logger.debug { "$name $ball" }
                delay(300)
                table.send(ball)
            }
        }

        runBlocking {
            val table = Channel<Ball>()

            launch { player("ping", table) }
            launch { player("pong", table) }

            logger.debug { "Start ping pong" }
            table.send(Ball(0))
            delay(1000)
            coroutineContext.cancelChildren()
            logger.debug { "Finish ping pong" }
        }
    }

}