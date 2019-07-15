package io.github.debop.futures.guava

import com.google.common.util.concurrent.AbstractFuture
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

/**
 * [ListenableFuture]를 [CompletableFuture]로 변환합니다.
 */
fun <T> ListenableFuture<T>.toCompletableFuture(): CompletableFuture<T> {
    val future = CompletableFuture<T>()

    this.onComplete(
        executor = DirectExecutor,
        onFailure = { future.completeExceptionally(it) },
        onSuccess = { future.complete(it) }
    )

    return future
}

/**
 * [CompletableFuture]를 [ListenableFuture]로 변환합니다
 */
fun <T> CompletableFuture<T>.toListenableFuture(): ListenableFuture<T> =
    CompletableToListenableFutureWrapper<T>(this)

open class CompletableToListenableFutureWrapper<T>(val completableFuture: CompletableFuture<T>): AbstractFuture<T>(), BiConsumer<T, Throwable?> {

    init {
        completableFuture.whenComplete { result, error -> accept(result, error) }
    }

    override fun accept(result: T, error: Throwable?) {
        if (error != null) {
            setException(error)
        } else {
            set(result)
        }
    }
}