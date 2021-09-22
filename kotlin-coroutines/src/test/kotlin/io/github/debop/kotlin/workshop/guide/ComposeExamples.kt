package io.github.debop.kotlin.workshop.guide

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeLessThan
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class ComposeExamples {

    companion object: KLogging()

    @Test
    fun `compose two suspend function in blocking`() {

        suspend fun doOne(): Int {
            delay(1000L)
            return 13
        }

        suspend fun doTwo(): Int {
            delay(1000L)
            return 29
        }

        runBlocking {
            val time = measureTimeMillis {
                val one = doOne()
                val two = doTwo()
                logger.debug { "The  answer is ${one + two}" }
            }
            logger.debug { "Elapsed time=$time" }
            time shouldBeGreaterThan 2000
        }
    }

    @Test
    fun `compose two suspend function in non-blocking`() {
        suspend fun doOne(): Int {
            delay(1000L)
            return 13
        }

        suspend fun doTwo(): Int {
            delay(1000L)
            return 29
        }

        runBlocking {
            val time = measureTimeMillis {
                val one = async { doOne() }
                val two = async { doTwo() }
                logger.debug("The  answer is ${one.await() + two.await()}")
            }
            logger.debug { "Elapsed time=$time" }
            time shouldBeGreaterOrEqualTo 1000 shouldBeLessThan 2000
        }
    }

    @Test
    fun `compose two suspend function in non-blocking with lazy start`() {
        suspend fun doOne(): Int {
            delay(1000L)
            return 13
        }

        suspend fun doTwo(): Int {
            delay(1000L)
            return 29
        }

        runBlocking {
            val time = measureTimeMillis {
                val one = async(start = CoroutineStart.LAZY) { doOne() }
                val two = async(start = CoroutineStart.LAZY) { doTwo() }
                one.start()
                two.start()
                logger.debug("The  answer is ${one.await() + two.await()}")
            }
            logger.debug { "Elapsed time=$time" }
            time shouldBeGreaterOrEqualTo 1000 shouldBeLessThan 2000
        }
    }

    @Test
    fun `funtion with return deferred`() {
        suspend fun doOne(): Int {
            delay(1000L)
            return 13
        }

        suspend fun doTwo(): Int {
            delay(1000L)
            return 29
        }

        val scope = CoroutineScope(Dispatchers.IO)

        fun doOneAsync(): Deferred<Int> = scope.async { doOne() }
        fun doTwoAsync(): Deferred<Int> = scope.async { doTwo() }


        val time = measureTimeMillis {
            val one = doOneAsync()
            val two = doTwoAsync()

            // suspend function인 await 있는 부분만 runBlocking 으로 감싼다
            runBlocking {
                logger.debug("The  answer is ${one.await() + two.await()}")
            }
        }
        logger.debug { "Elapsed time=$time" }
        time shouldBeGreaterOrEqualTo 1000 shouldBeLessThan 2000
    }

    @Test
    fun `compose using coroutineScope`() {
        suspend fun doOne(): Int {
            delay(1000L)
            return 13
        }

        suspend fun doTwo(): Int {
            delay(1000L)
            return 29
        }

        suspend fun concurrentSum(): Int = coroutineScope {
            val one = async { doOne() }
            val two = async { doTwo() }
            one.await() + two.await()
        }
        runBlocking {
            val time = measureTimeMillis {
                logger.debug("The  answer is ${concurrentSum()}")
            }
            logger.debug { "Elapsed time=$time" }
            time shouldBeGreaterOrEqualTo 1000 shouldBeLessThan 2000
        }
    }

    @Test
    fun `compose two suspend function with raise exception`() {

        suspend fun failedConcurrentSum(): Int = coroutineScope {

            val one = async<Int> {
                try {
                    delay(Long.MAX_VALUE)
                    42
                } finally {
                    logger.debug { "First child was cancelled" }
                }
            }

            val two = async<Int> {
                logger.debug { "Second child throws an exception" }
                throw ArithmeticException()
            }

            one.await() + two.await()
        }

        runBlocking {
            try {
                failedConcurrentSum()
            } catch (e: ArithmeticException) {
                logger.debug { "Computation failed with ArithmeticException" }
            }
        }
    }
}