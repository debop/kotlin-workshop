package io.github.debop.kotlin.workshop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.yield
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * [Future] 를 Coroutine scope 하에서 실행되기를 기다린다.
 *
 * @param T
 * @return
 */
suspend fun <T: Any> Future<T>.await(): T =
    suspendCancellableCoroutine { cont ->
        while(true) {
            if(isDone) {
                try {
                    val result = get()
                    cont.resume(result)
                    break
                } catch(e: ExecutionException) {
                    cont.resumeWithException(e.cause ?: e)
                    break
                }
            } else if(isCancelled) {
                cont.cancel(null)
                break
            }
            Thread.sleep(1L)
        }
    }


/**
 * [Future] 를 [CompletableFuture] 로 변환하도록 한다
 *
 * @param T
 * @return
 */
fun <T: Any> Future<T>.asCompletableFuture(): CompletableFuture<T> = CompletablePromise(this)

/**
 * Future 를 Non-Blocking이 될 수 있도록 [CompletableFuture] 로 변환한다
 * 이를 [await] 를 메소드를 이용하면 Coroutines 에서도 사용 가능한다
 *
 * @param T
 * @property future
 */
private class CompletablePromise<T: Any>(val future: Future<T>): CompletableFuture<T>(), CoroutineScope {

    val job = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    override val coroutineContext: CoroutineContext get() = job

    init {
        tryToComplete()
    }

    private fun tryToComplete() {
        runBlocking(context = coroutineContext) {
            while(true) {
                if(future.isDone) {
                    try {
                        val result = future.get()
                        complete(result)
                    } catch(e: ExecutionException) {
                        completeExceptionally(e.cause ?: e)
                    }
                    break
                } else if(future.isCancelled) {
                    cancel(true)
                    this.cancel(null)
                    break
                }
                yield()
            }
        }
    }
}