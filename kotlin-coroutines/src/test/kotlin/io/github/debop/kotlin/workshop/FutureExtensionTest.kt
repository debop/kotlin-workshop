package io.github.debop.kotlin.workshop

import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import mu.KLogging
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 * FutureExtensionTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 17
 */
class FutureExtensionTest {

    companion object: KLogging()

    val service = Executors.newSingleThreadExecutor()

    private fun createFuture(): Future<String> {
        return service.submit<String> {
            Thread.sleep(1000L)
            "success"
        }
    }

    @Test
    fun `convert future to completable future`() {
        val stringFuture = createFuture()
        logger.debug { "Start future..." }

        val cf = stringFuture.asCompletableFuture()
        cf.whenComplete { result, error ->
            logger.debug { "result=$result" }
        }
        cf.join()
    }

    @Test
    fun `convert future to completable future and await`() {
        val stringFuture = createFuture()
        logger.debug { "Start future..." }

        runBlocking {
            val result = stringFuture.asCompletableFuture().await()
            logger.debug { "result=$result" }
        }
    }

    @Test
    fun `future with coroutine await`() {
        val stringFuture = createFuture()
        logger.debug { "Start future..." }

        runBlocking {
            val result = stringFuture.await()
            logger.debug { "result=$result" }
        }
    }

    @Test
    fun `future with coroutine await and timeout`() {
        val stringFuture = createFuture()
        logger.debug { "Start future..." }

        runBlocking {
            val result = withTimeoutOrNull(100) { stringFuture.await() }
            logger.debug { "result=$result" }
        }
    }
}