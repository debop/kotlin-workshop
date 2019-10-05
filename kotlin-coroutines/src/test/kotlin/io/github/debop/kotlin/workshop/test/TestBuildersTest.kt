package io.github.debop.kotlin.workshop.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import mu.KLogging
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import kotlin.coroutines.ContinuationInterceptor

/**
 * TestBuildersTest
 *
 * @author debop
 */
@Suppress("EXPERIMENTAL_API_USAGE")
class TestBuildersTest {

    companion object: KLogging()

    @Test
    fun `scope run blocking - passes Dispatcher`() {
        val scope = TestCoroutineScope()
        scope.runBlockingTest {
            coroutineContext[ContinuationInterceptor] shouldEqual scope.coroutineContext[ContinuationInterceptor]
        }
    }

    @Test
    fun `dispatcher run blocking - passes dispatcher`() {
        val dispatcher = TestCoroutineDispatcher()
        dispatcher.runBlockingTest {
            coroutineContext[ContinuationInterceptor] shouldEqual dispatcher
        }
    }

    @Test
    fun `scope run blocking - advanced previous delay`() {
        val scope = TestCoroutineScope()
        val deferred = scope.async {
            delay(SLOW)
            3
        }

        scope.runBlockingTest {
            assertRunsFast {
                deferred.await() shouldEqualTo 3
            }
        }
    }

    @Test
    fun `dispatcher run blocking - advanced previous delay`() {
        val dispatcher = TestCoroutineDispatcher()
        val scope = CoroutineScope(dispatcher)
        val deferred = scope.async {
            delay(SLOW)
            3
        }

        dispatcher.runBlockingTest {
            assertRunsFast {
                deferred.await() shouldEqualTo 3
            }
        }
    }

    @Test
    fun `scope run blocking - disables immediate on exit`() {
        val scope = TestCoroutineScope()
        scope.runBlockingTest {
            assertRunsFast {
                delay(SLOW)
            }
        }

        val deferred = scope.async {
            delay(SLOW)
            3
        }

        scope.runCurrent()
        deferred.isActive.shouldBeTrue()

        // Immediately execute all pending tasks and advance the virtual clock-time to the last delay.
        scope.advanceUntilIdle()
        deferred.getCompleted() shouldEqualTo 3
    }

    @Test
    fun `when in async - runBlocking nests properly`() {

        val dispatcher = TestCoroutineDispatcher()
        val scope = TestCoroutineScope(dispatcher)
        val deferred = scope.async {
            delay(1_000)
            var retval = 2
            runBlockingTest {
                delay(1_000)
                retval++
            }
            retval
        }

        scope.advanceTimeBy(1_000)
        scope.launch {
            assertRunsFast {
                deferred.getCompleted() shouldEqualTo 3
            }
        }
        scope.runCurrent()
        scope.cleanupTestCoroutines()
    }

    @Test
    fun `when in runBlocking runBlockingTest nests properly`() {

        val scope = TestCoroutineScope()
        var calls = 0

        scope.runBlockingTest {
            delay(1_000)
            calls++
            runBlockingTest {
                val job = launch {
                    delay(1_000)
                    calls++
                }

                job.isActive.shouldBeTrue()
                advanceUntilIdle()
                job.isActive.shouldBeFalse()
                calls++
            }
            ++calls
        }
        calls shouldEqualTo 4
    }
}