package io.github.debop.kotlin.workshop.examples.basic.step2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging.logger
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeLessThan
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.system.measureTimeMillis

class ComposeExamples {

    val log = logger { }

    suspend fun doSomethingOne(): Int {
        log.trace { "Something One" }
        delay(1000)
        return 13
    }

    suspend fun doSomethingTwo(): Int {
        log.trace { "Something Two" }
        delay(1000)
        return 29
    }

    @Nested
    inner class Step01 {

        @Test
        fun `같은 Coroutines 에서 실행`() = runBlocking<Unit> {
            val time = measureTimeMillis {
                val one = doSomethingOne()
                val two = doSomethingTwo()
                log.trace { "The answer is ${one + two}" }
            }
            log.debug { "Took in $time ms" }
            time shouldBeGreaterThan 2000
        }
    }

    @Nested
    inner class Step02 {

        @Test
        fun `async를 통해 다른 Coroutine 에서 실행`() = runBlocking<Unit> {
            val time = measureTimeMillis {
                val one = async { doSomethingOne() }
                val two = async { doSomethingTwo() }
                log.trace("The answer is ${one.await() + two.await()}")
            }
            log.debug { "Took in $time ms" }
            time shouldBeLessThan 1500
        }
    }

    @Nested
    inner class Step03 {
        @Test
        fun `async with lazy start`() = runBlocking<Unit> {
            val time = measureTimeMillis {
                val one = async(start = CoroutineStart.LAZY) { doSomethingOne() }
                val two = async(start = CoroutineStart.LAZY) { doSomethingTwo() }

                // 다른 작업 ...

                one.start()
                two.start()

                log.trace("The answer is ${one.await() + two.await()}")
            }
            log.debug { "Took in $time ms" }
            time shouldBeLessThan 1500
        }
    }

    @Nested
    inner class Step04: CoroutineScope by CoroutineScope(Dispatchers.IO) {

        private fun somethingUsefulOneAsync() = async {
            doSomethingOne()
        }

        private fun somethingUsefulTwoAsync() = async {
            doSomethingTwo()
        }

        @Test
        fun `async action outside of a coroutine`() {
            val time = measureTimeMillis {
                val one = somethingUsefulOneAsync()
                val two = somethingUsefulTwoAsync()

                runBlocking {
                    log.trace("The answer is ${one.await() + two.await()}")
                }
            }
            log.debug { "Took in $time ms" }
            time shouldBeLessThan 1500
        }
    }

    @Nested
    inner class Step05 {

        private suspend fun concurrentSum(): Int = coroutineScope {
            val one = async { doSomethingOne() }
            val two = async { doSomethingTwo() }
            one.await() + two.await()
        }

        @Test
        fun `combine suspend methods`() = runBlocking<Unit> {
            val time = measureTimeMillis {
                log.trace("The answer is ${concurrentSum()}")
            }
            log.debug { "Took in $time ms" }
            time shouldBeLessThan 1500
        }
    }

    @Nested
    inner class Step06 {

        private suspend fun failedConcurrentSum(): Int = coroutineScope {
            val one = async<Int> {
                try {
                    delay(Long.MAX_VALUE)
                    42
                } finally {
                    log.info { "First child was cancelled" }
                }
            }
            val two = async<Int> {
                log.debug { "Second child throws an exception" }
                throw ArithmeticException()
            }
            one.await() + two.await()
        }

        @Test
        fun `catch exception of child`() {
            assertThrows<ArithmeticException> {
                runBlocking<Unit> {
                    failedConcurrentSum()
                }
            }
        }
    }
}