package io.github.debop.kotlin.workshop.guide

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import mu.KLogging
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.System.currentTimeMillis

class CancelExamples: CoroutineScope by CoroutineScope(CoroutineName("example") + Dispatchers.IO) {

    companion object: KLogging()

    @Test
    fun `cancel job`() = runBlocking<Unit> {
        val job = launch {
            repeat(1000) {
                logger.debug { "job: I'm sleeping $it ..." }
                delay(500L)
            }
        }
        delay(1300L)
        logger.debug { "I'm tired of waiting!" }
        job.cancel()
        job.join()
        logger.debug { "Now I can quit." }
    }

    @Test
    fun `cancel and join job`() = runBlocking<Unit> {
        val startTime = currentTimeMillis()

        val job = launch(Dispatchers.Default) {
            var nextTime = startTime
            var i = 0
            while (isActive) {
                // print a message twice a second
                if (currentTimeMillis() > nextTime) {
                    logger.debug { "job: I'm sleeping ${i++}" }
                    nextTime += 500L
                }
            }
        }
        delay(1300L) // delay a bit
        logger.debug { "I'm tired of waiting!" }
        job.cancelAndJoin()
        logger.debug { "Now I can quit." }
    }

    @Test
    fun `cancel and cleanup`() = runBlocking<Unit> {
        val job = launch {
            try {
                repeat(1000) {
                    logger.debug { "job: I'm sleeping $it ..." }
                    delay(500L)
                }
            } finally {
                logger.debug { "I'm running finally" }
            }
        }
        delay(1300L)
        logger.debug { "I'm tired of waiting!" }
        job.cancelAndJoin()
        logger.debug { "Now I can quit." }
    }

    @Test
    fun `cancel and cleanup with NonCancellable`() = runBlocking<Unit> {
        val job = launch {
            try {
                repeat(1000) {
                    logger.debug { "job: I'm sleeping $it ..." }
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    logger.debug { "I'm running finally" }
                    delay(1000L)
                    logger.debug { "And I've just delayed for 1 sec because I'm non-cancellable" }
                }
            }
        }
        delay(1300L)
        logger.debug { "I'm tired of waiting!" }
        job.cancelAndJoin()
        logger.debug { "Now I can quit." }
    }

    @Test
    fun `cancel job with timeout`() {
        assertThrows<TimeoutCancellationException> {
            runBlocking<Unit> {
                withTimeout(1300L) {
                    repeat(1000) {
                        logger.debug { "I'm sleeping $it ..." }
                        delay(500L)
                    }
                }
            }
        }
    }

    @Test
    fun `cancel job with timeout or null`() = runBlocking<Unit> {

        val result = withTimeoutOrNull(1300L) {
            repeat(1000) {
                logger.debug { "I'm sleeping $it ..." }
                delay(500L)
            }
            "Done"
        }
        result.shouldBeNull()
        logger.debug { "Result is $result" }
    }
}