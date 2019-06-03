package io.github.debop.kotlin.workshop

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// SEE kotlinx-coroutines-jdk8 module. CompletableFutureCoroutine
/*
/**
 * Awaits for completion of the completion stage without blocking a thread.
 *
 * This suspending function is cancellable.
 * If the [Job] of the current coroutine is cancelled or completed while this suspending function is waiting, this function
 * stops waiting for the completion stage and immediately resumes with [CancellationException][kotlinx.coroutines.CancellationException].
 * This method is intended to be used with one-shot futures, so on coroutine cancellation completion stage is cancelled as well if it is instance of [CompletableFuture].
 * If cancelling given stage is undesired, `stage.asDeferred().await()` should be used instead.
 */
public suspend fun <T> CompletionStage<T>.await(): T {
    // fast path when CompletableFuture is already done (does not suspend)
    if (this is Future<*> && isDone()) {
        try {
            @Suppress("UNCHECKED_CAST")
            return get() as T
        } catch (e: ExecutionException) {
            throw e.cause ?: e // unwrap original cause from ExecutionException
        }
    }
    // slow path -- suspend
    return suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
        val consumer = ContinuationConsumer(cont)
        whenComplete(consumer)
        cont.invokeOnCancellation {
            // mayInterruptIfRunning is not used
            (this as? CompletableFuture<T>)?.cancel(false)
            consumer.cont = null // shall clear reference to continuation to aid GC
        }
    }
}

private class ContinuationConsumer<T>(
    @Volatile @JvmField var cont: Continuation<T>?
) : BiConsumer<T?, Throwable?> {
    @Suppress("UNCHECKED_CAST")
    override fun accept(result: T?, exception: Throwable?) {
        val cont = this.cont ?: return // atomically read current value unless null
        if (exception == null) {
            // the future has been completed normally
            cont.resume(result as T)
        } else {
            // the future has completed with an exception, unwrap it to provide consistent view of .await() result and to propagate only original exception
            cont.resumeWithException((exception as? CompletionException)?.cause ?: exception)
        }
    }
}
*/

@ExperimentalCoroutinesApi
suspend fun <T> CompletableFuture<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        this@await.whenComplete { result, error ->
            when {
                this@await.isCancelled -> cont.cancel(error)
                error != null -> cont.resumeWithException(error)
                else -> cont.resume(result)
            }
        }
    }
}