package io.github.debop.kotlin.workshop

import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

/**
 * FutureExtensionTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 17
 */
class FutureExtensionTest {

    companion object: KLogging()

    @Test
    fun `01 convert future to completable future`() {
        val service = Executors.newSingleThreadExecutor()
        val stringFuture = service.submit<String> {
            Thread.sleep(1000L)
            "success"
        }
        logger.debug { "Start future..." }

        val cf = stringFuture.toCompletableFuture()
        cf.whenComplete { result, error ->
            logger.debug { "result=$result" }
        }
        cf.join()
    }

    @Test
    fun `02 convert future to completable future and await`() {
        val service = Executors.newSingleThreadExecutor()
        val stringFuture = service.submit<String> {
            Thread.sleep(1000)
            "success"
        }
        logger.debug { "Start future..." }

        runBlocking {
            val result = stringFuture.toCompletableFuture().await()
            logger.debug { "result=$result" }
        }

    }

    @Test
    fun `03 future with coroutine await`() {
        val service = Executors.newSingleThreadExecutor()
        val future = service.submit<String> {
            Thread.sleep(1000)
            "success"
        }
        logger.debug { "Start future..." }

        runBlocking {
            val result = future.await()
            logger.debug { "result=$result" }
        }
    }
}