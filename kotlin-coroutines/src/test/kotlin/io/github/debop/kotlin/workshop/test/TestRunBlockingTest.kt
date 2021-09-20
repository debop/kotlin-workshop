package io.github.debop.kotlin.workshop.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withTimeout
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * runBlockingTest 는 runBlocking 과 유사하지만, delays를 실제 시각만큼 지연시키는 것이 아니라 바로 진행해버린다
 *
 */
@ExperimentalCoroutinesApi
class TestRunBlockingTest {

    @Test
    fun `delay advances time automatically`() = runBlockingTest {
        assertRunsFast {
            delay(SLOW)
        }
    }

    @Test
    fun `calling suspend with delay`() = runBlockingTest {
        suspend fun withDelay(): Int {
            delay(SLOW)
            return 3
        }

        assertRunsFast {
            withDelay() shouldBeEqualTo 3
        }
    }

    @Test
    fun `launch - advancesAutomatically`() = runBlockingTest {
        val job = launch {
            delay(SLOW)
        }
        assertRunsFast {
            job.join()
            job.isCompleted.shouldBeTrue()
        }
    }

    @Test
    fun `when using timeout - triggers when delayed`() {
        assertThrows<IllegalStateException> {
            runBlockingTest {
                assertRunsFast {
                    withTimeout(SLOW) {
                        delay(SLOW)
                    }
                }
            }
        }
    }
}