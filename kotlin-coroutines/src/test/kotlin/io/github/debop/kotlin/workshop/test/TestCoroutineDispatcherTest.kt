package io.github.debop.kotlin.workshop.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.withContext
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotEqual
import org.junit.jupiter.api.Test

@Suppress("EXPERIMENTAL_API_USAGE")
class TestCoroutineDispatcherTest {

    companion object: KLogging()

    @Test
    fun `when dispatcher resumed does auto progress current`() {
        val subject = TestCoroutineDispatcher()
        val scope = CoroutineScope(subject)
        var executed = 0
        scope.launch {
            executed++
        }
        executed shouldEqualTo 1
    }

    @Test
    fun `when dispatcher resumed does not auto progress time`() {
        val subject = TestCoroutineDispatcher()
        val scope = CoroutineScope(subject)
        var executed = 0
        scope.launch {
            delay(1_000)
            executed++
        }
        executed shouldEqualTo 0
        subject.advanceUntilIdle()
        executed shouldEqualTo 1
    }

    @Test
    fun `when dispatcher paused then resumed does auto progress current`() {
        val subject = TestCoroutineDispatcher()
        subject.pauseDispatcher()
        val scope = CoroutineScope(subject)
        var executed = 0
        scope.launch {
            executed++
        }
        executed shouldEqualTo 0
        subject.resumeDispatcher()
        executed shouldEqualTo 1
    }

    @Test
    fun `when dispatch called runs on current thread`() {
        val currentThread = Thread.currentThread()
        val subject = TestCoroutineDispatcher()
        val scope = TestCoroutineScope(subject)

        val deferred = scope.async(Dispatchers.Default) {
            withContext(subject) {
                Thread.currentThread() shouldNotEqual currentThread
                3
            }
        }

        runBlocking {
            deferred.await() shouldEqualTo 3
        }
    }

    @Test
    fun `when all dispatchers mocked - runs on same thread`() {
        val currentThread = Thread.currentThread()
        val subject = TestCoroutineDispatcher()
        val scope = TestCoroutineScope(subject)

        val deferred = scope.async(subject) {
            withContext(subject) {
                Thread.currentThread() shouldEqual currentThread
                3
            }
        }

        runBlocking {
            deferred.await() shouldEqualTo 3
        }
    }
}