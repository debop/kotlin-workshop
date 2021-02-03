package io.github.debop.kotlin.workshop.examples.basic.step2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import mu.KLogging
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
@DisplayName("Channel examples")
class ChannelExamples {

    companion object : KLogging()

    @Test
    fun `send data via channel`() = runBlocking {
        val channel = Channel<Int>()
        launch {
            repeat(5) {
                logger.trace { "Send $it" }
                channel.send(it)
            }
        }

        repeat(5) {
            val received = channel.receive()
            logger.trace { "Received $received" }
        }
        logger.debug { "Done!" }
    }

    @Test
    fun `send data and close`() = runBlocking {
        val channel = Channel<Int>()

        launch {
            for (x in 1..5) {
                logger.trace { "Send $x" }
                channel.send(x)
            }
            channel.close() // 종료임을 알린다. onComplete 와 같다
        }

        // Channel 이 close 될 때까지 for 구문을 실행시킨다
        for (y in channel) {
            logger.trace { "Received: $y" }
        }
        logger.debug { "Done!" }
    }

    fun CoroutineScope.produceSquare(): ReceiveChannel<Int> = produce {
        for (x in 1..5) {
            logger.trace { "Send: square of $x" }
            send(x * x)
        }
    }

    @Test
    fun `produce and consume via ReceiveChannel`() = runBlocking {
        val squares = produceSquare()
        squares.consumeEach {
            logger.trace { "Received: $it" }
        }
        logger.debug { "Done!" }
    }

    @Nested
    inner class Step04 {

        fun CoroutineScope.produceNumbers() = produce {
            var x = 1
            while (true) {
                send(x++)
            }
        }

        fun CoroutineScope.square(numbers: ReceiveChannel<Int>) = produce {
            for (x in numbers) {
                send(x * x)
            }
        }

        // Flow 로 대체 
        //        @Test
        //        fun `consume chained channels`() = runBlocking {
        //            val numbers = produceNumbers()
        //            val squares = square(numbers)
        //
        //            squares.take(5).consumeEach {
        //                logger.trace { "Received: $it" }
        //            }
        //
        //            logger.debug { "Done!" }
        //
        //            coroutineContext.cancelChildren() // cancel children coroutines (produceNumbers)
        //        }
    }

    @Nested
    inner class Step05 {

        fun CoroutineScope.numbersFrom(start: Int) = produce {
            var x = start
            while (true) {
                send(x++)
            }
        }

        fun CoroutineScope.filter(numbers: ReceiveChannel<Int>, prime: Int) = produce {
            for (x in numbers) {
                if (x % prime != 0) {
                    send(x)
                }
            }
        }

        @Test
        fun `filter prime numbers`() = runBlocking {
            var cur = numbersFrom(2)
            for (i in 1..10) {
                val prime = cur.receive()
                logger.trace { "prime=$prime" }
                cur = filter(cur, prime)
            }
            coroutineContext.cancelChildren()
        }
    }

    @Nested
    @DisplayName("Launch processes")
    inner class Step06 {
        fun CoroutineScope.produceNumbers() = produce {
            var x = 1
            while (true) {
                send(x++)
                delay(100)
            }
        }

        fun CoroutineScope.launchProcess(id: Int, channel: ReceiveChannel<Int>) = launch {
            for (msg in channel) {
                logger.trace { "Processor #$id received $msg" }
            }
        }

        @Test
        fun `launch multiple processes`() = runBlocking {
            val producer = produceNumbers()
            repeat(5) {
                launchProcess(it, producer)
            }
            delay(950)
            producer.cancel()  // when procuder is cancelled, launchProcess job is stopped
        }
    }

    @Nested
    inner class Step07 {

        val log = logger()

        suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
            while (true) {
                delay(time)
                channel.send(s)
                log.trace { "Send $s" }
            }
        }

        @Test
        fun `send via SendChannel`() = runBlocking {
            val channel = Channel<String>()

            launch { sendString(channel, "Hello", 200) }
            launch { sendString(channel, "World!", 500) }

            repeat(6) {
                val received = channel.receive()
                log.trace { "Received: $received" }
            }

            coroutineContext.cancelChildren()
        }
    }

    @Nested
    inner class Step08 {
        val log = logger()

        @Test
        fun `specify capacity with channel`() = runBlocking {
            val channel = Channel<Int>(4)
            val sender = launch {
                repeat(100) {
                    log.trace { "Sending $it" }
                    channel.send(it)
                }
            }
            // channel로부터 정보를 받지 않고, 기다리면 channel buffer를 채우고, 기다린다.
            delay(1000)
            sender.cancel()
        }
    }

    data class Ball(var hits: Int)

    /**
     * 복수의 Job이 Channel 을 공유하여, 서로 데이터를 발송하고, 수신한다.
     */
    @Nested
    inner class Step09 {
        val log = logger()

        suspend fun player(name: String, table: Channel<Ball>) {
            // 데이터를 수신하여 처리한 후, 다시 발신한다.
            for (ball in table) {
                ball.hits++
                log.trace { "$name $ball" }
                //delay(100)
                table.send(ball)
            }
        }

        @Test
        fun `two job share channel`() = runBlocking {
            val table = Channel<Ball>()
            launch(Dispatchers.IO) { player("ping", table) }
            launch(Dispatchers.IO) { player("pong", table) }

            table.send(Ball(0))

            delay(1000)
            coroutineContext.cancelChildren()
        }
    }

    @Nested
    inner class TickerExample {

        val log = logger()

        @Test
        fun `ticker example`() = runBlocking {
            val tickerChannel = ticker(100, 0)

            var nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
            log.trace { "첫번째 요소는 즉시 가능하다: $nextElement" }

            nextElement = withTimeoutOrNull(50) { tickerChannel.receive() }
            log.trace { "다음 요소는 50ms 안에 준비되지 않습니다: $nextElement" }
            nextElement.shouldBeNull()

            nextElement = withTimeoutOrNull(80) { tickerChannel.receive() }
            log.trace { "다음 요소는 100ms 안에 준비됩니다: $nextElement" }

            // 오래 기다리면 ticker 가 충분히 많은 요소를 생성합니다.
            log.trace { "Consumer pauses for 200ms" }
            delay(200)

            nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
            log.trace { "다음 요소는 즉시 가능하다: $nextElement" }

            nextElement = withTimeoutOrNull(100) { tickerChannel.receive() }
            log.trace { "다음 요소는 오랜 기간 기다렸기 때문에 즉시 가능하다: $nextElement" }
            nextElement.shouldNotBeNull()

            tickerChannel.cancel()
        }
    }
}