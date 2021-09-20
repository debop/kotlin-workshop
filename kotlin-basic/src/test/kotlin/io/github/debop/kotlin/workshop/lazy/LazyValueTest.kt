package io.github.debop.kotlin.workshop.lazy

import io.github.debop.kotlin.tests.extensions.Randomized
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.io.Serializable

/**
 * LazyValueTest
 * @author debop (Sunghyouk Bae)
 */
@Randomized
class LazyValueTest {

    companion object : KLogging()

    @Test
    fun `compute when not assigned`() {
        val time = System.nanoTime()
        val lazyVal = LazyValue {
            Thread.sleep(10)
            time
        }

        lazyVal.isInitialized.shouldBeFalse()
        // initialize when access value
        lazyVal.value shouldBeEqualTo time
        lazyVal.isInitialized.shouldBeTrue()

        // already initialized
        Thread.sleep(10)
        lazyVal.value shouldBeEqualTo time
    }

    @RepeatedTest(5)
    fun `compute lazy value in asynchronous`() {
        val x = LazyValue { System.nanoTime() }

        runBlocking {
            val startTime = System.nanoTime()

            val lazyValue = async {
                delay(100)
                x.value
            }

            val time = lazyValue.await()
            time shouldBeGreaterThan startTime
        }
    }

    @RepeatedTest(5)
    fun `copy value object`() {
        val person = Person(1, "debop", 51)

        val str1 = person.toString()
        val str2 = person.toString()

        person.callCount shouldBeEqualTo 1
        str1 shouldBeEqualTo str2
    }

    data class Person(val id: Long, val name: String, val age: Int = 0) : Serializable {

        var callCount = 0
        private val toStringValue = LazyValue {
            callCount++
            "Person(id=$id, name=$name, age=$age)"
        }

        override fun toString(): String = toStringValue.value
    }
}