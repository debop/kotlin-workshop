package io.github.debop.kotlin.workshop.examples.basic.step2

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

/**
 * CancelExamples
 * @author debop (Sunghyouk Bae)
 */
class CancelExamples {

    companion object : KLogging()

    @Test
    fun `run job and cancel job`() = runBlocking<Unit> {

        val job = launch {
            repeat(1000) {
                logger.debug { "job: I'm sleeping $it..." }
                delay(500L)
            }
        }
        delay(1300) // delay for job running (3번 실행할 시간)
        logger.debug { "main: 대기 중 ..." }
        job.cancelAndJoin()
        logger.debug { "main: 종료" }
    }

    @Test
    fun `run job with Default dispatchers and cancel job but not notify`() = runBlocking<Unit> {

        val startTime = System.currentTimeMillis()

        // log의 thread name 을 보세요 (main thread와 다른 DefaultDispatcher Thread 하의 coroutine에서 실행됩니다.
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (i < 5) {  // NOTE: cancel 을 호출해도 계속 실행됩니다.
                if (System.currentTimeMillis() >= nextPrintTime) {
                    logger.debug { "job: I'm sleeping ${i++}..." }
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300) // delay for job running (3번 실행할 시간)
        logger.debug { "main: 대기 중 ..." }
        job.cancelAndJoin()                     // cancel을 호출해도 5번 모두 실행됩니다.
        logger.debug { "main: 종료" }
    }

    @Test
    fun `run job with Default dispatchers and cancel job`() = runBlocking<Unit> {

        val startTime = System.currentTimeMillis()

        // log의 thread name 을 보세요 (main thread와 다른 DefaultDispatcher Thread 하의 coroutine에서 실행됩니다.
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = startTime
            var i = 0
            while (isActive) {  // NOTE: cancel이 호출되면 isActive는 false를 반환합니다
                if (System.currentTimeMillis() >= nextPrintTime) {
                    logger.debug { "job: I'm sleeping ${i++}..." }
                    nextPrintTime += 500L
                }
            }
        }
        delay(1300) // delay for job running (3번 실행할 시간)
        logger.debug { "main: 대기 중 ..." }
        job.cancelAndJoin()             // cancel 을 호출하면 isActive가 false가 되므로 곧바로 중단됩니다.
        logger.debug { "main: 종료" }
    }

    @Test
    fun `when cancel, cleanup`() = runBlocking<Unit> {
        val job = launch {
            try {
                repeat(1000) {
                    logger.debug("job: I'm sleeping $it ...")
                    delay(500)
                }
            } finally {
                // Cancel 호출 시에도 마무리 작업을 수행해야 할 경우
                withContext(NonCancellable) {
                    logger.debug("job: 마무리 작업 중...")
                    delay(1000)
                    logger.debug("job: 마무리 작업 완료")
                }
            }
        }

        delay(1300)
        logger.debug { "main: 대기 중 ... cancel 호출 ..." }
        job.cancelAndJoin()
        logger.debug { "main: 종료" }
    }

    @Test
    fun `when timeout, raise timeout cancel exception`() {
        assertThrows<TimeoutCancellationException> {
            runBlocking<Unit> {
                withTimeout(1300) {
                    repeat(1000) {
                        logger.debug("job: I'm sleeping $it ...")
                        delay(500)
                    }
                }
            }
        }
    }

    @Test
    fun `when timeout, return null`() = runBlocking<Unit> {
        val result = withTimeoutOrNull(1300) {
            repeat(1000) {
                logger.debug("job: I'm sleeping $it ...")
                delay(500)
            }
            "Done"
        }
        result.shouldBeNull()
    }
}