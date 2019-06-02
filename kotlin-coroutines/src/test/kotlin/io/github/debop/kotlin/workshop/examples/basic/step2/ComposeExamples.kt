package io.github.debop.kotlin.workshop.examples.basic.step2

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging.logger
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeLessThan
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

/**
 * ComposeExamples
 * @author debop (Sunghyouk Bae)
 */
class ComposeExamples {

    val log = logger { }

    @Nested
    inner class Step01 {

        suspend fun doSomethingOne(): Int {
            delay(1000)
            return 13
        }

        suspend fun doSomethingTwo(): Int {
            delay(1000)
            return 29
        }

        @Test
        fun `call multiple suspend method`() = runBlocking<Unit> {
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

        suspend fun doSomethingOne(): Int {
            delay(1000)
            return 13
        }

        suspend fun doSomethingTwo(): Int {
            delay(1000)
            return 29
        }

        @Test
        fun `call multiple suspend method`() = runBlocking<Unit> {
            val time = measureTimeMillis {
                val one = async { doSomethingOne() }
                val two = async { doSomethingTwo() }
                log.trace("The answer is ${one.await() + two.await()}")
            }
            log.debug { "Took in $time ms" }
            time shouldBeLessThan 1500
        }
    }
}