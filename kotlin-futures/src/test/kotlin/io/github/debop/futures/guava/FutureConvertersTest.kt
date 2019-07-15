package io.github.debop.futures.guava

import com.google.common.util.concurrent.ListenableFuture
import io.github.debop.futures.jdk8.asCompletableFuture
import io.github.debop.futures.jdk8.futureOf
import io.github.debop.futures.jdk8.recover
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CompletableFuture
import java.util.function.Function

/**
 * FutureConvertersTest
 *
 * @author debop
 * @since 19. 7. 15
 */
class FutureConvertersTest {

    val success: ListenableFuture<Int> = listenableFuture { 1 }
    val failed: ListenableFuture<Int> = IllegalArgumentException().asListenableFuture<Int>()

    @Test
    fun `성공한 ListenableFuture를 CompletableFuture로 변환`() {
        success.toCompletableFuture().get() shouldEqualTo 1

        success.toCompletableFuture()
            .thenApplyAsync(Function<Int, Int> { it + it }, ForkJoinExecutor)
            .get() shouldEqualTo 2
    }

    @Test
    fun `실패한 ListenableFuture를 CompletableFuture로 변환`() {

        assertThrows<Exception> {
            failed.toCompletableFuture().get()
        }.cause shouldBeInstanceOf IllegalArgumentException::class

        failed.toCompletableFuture().recover { 2 }.get() shouldEqualTo 2
    }

    private fun successCompletableFuture(value: Int = 42): CompletableFuture<Int> =
        futureOf { Thread.sleep(100); value }

    private fun failedCompletableFuture(): CompletableFuture<Int> =
        IllegalArgumentException().asCompletableFuture()

    @Test
    fun `성공한 CompletableFuture를 ListenableFuture로 변환`() {
        successCompletableFuture().toListenableFuture().get() shouldEqualTo 42
        successCompletableFuture().toListenableFuture().map { it + it }.get() shouldEqualTo 84
    }

    @Test
    fun `실패한 CompletableFuture를 ListenableFuture로 변환`() {
        assertThrows<Exception> {
            failedCompletableFuture().toListenableFuture().get()
        }.cause shouldBeInstanceOf IllegalArgumentException::class
    }
}