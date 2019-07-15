package io.github.debop.futures.guava

import com.google.common.util.concurrent.AsyncFunction
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import java.util.concurrent.Callable
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import com.google.common.base.Function as GuavaFunc

inline fun <V> listenableFuture(executor: ExecutorService = ForkJoinExecutor, crossinline block: () -> V): ListenableFuture<V> {
    val service = MoreExecutors.listeningDecorator(executor)
    return service.submit(Callable { block() })
}

inline fun <V> immediateFuture(crossinline block: () -> V): ListenableFuture<V> =
    listenableFuture(DirectExecutor, block)

fun <V> V.asListenableFuture(): ListenableFuture<V> = Futures.immediateFuture(this)

fun <V> Throwable.asListenableFuture(): ListenableFuture<V> = Futures.immediateFailedFuture(this)

inline fun <T, R> ListenableFuture<T>.map(executor: Executor = ForkJoinExecutor,
                                          crossinline function: (T) -> R): ListenableFuture<R> =
    Futures.transform(this, GuavaFunc { function(it!!) }, executor)

inline fun <T, R> ListenableFuture<T>.flatMap(executor: Executor = ForkJoinExecutor,
                                              crossinline function: (T) -> ListenableFuture<R>): ListenableFuture<R> =
    Futures.transformAsync(this, AsyncFunction { function(it!!) }, executor)

fun <V> ListenableFuture<ListenableFuture<V>>.flatten(): ListenableFuture<V> = flatMap { it }

inline fun <V> ListenableFuture<V>.filter(executor: Executor = ForkJoinExecutor,
                                          crossinline predicate: (V) -> Boolean): ListenableFuture<V> =
    flatMap(executor) {
        if (predicate(it)) this
        else NoSuchElementException("ListenableFuture.filter predicate is not satisfied").asListenableFuture<V>()
    }

inline fun <T, U, R> ListenableFuture<T>.zip(other: ListenableFuture<U>,
                                             executor: Executor = ForkJoinExecutor,
                                             crossinline zipper: (T, U) -> R): ListenableFuture<R> =
    flatMap(executor) { t -> other.map(executor) { u -> zipper(t, u) } }

fun <T, U> ListenableFuture<T>.zip(other: ListenableFuture<U>,
                                   executor: Executor = ForkJoinExecutor): ListenableFuture<Pair<T, U>> =
    zip(other, executor) { t, u -> t to u }

inline fun <T> ListenableFuture<T>.recover(executor: Executor = ForkJoinExecutor,
                                           crossinline func: (Throwable) -> T): ListenableFuture<T> =
    Futures.catching(this,
                     Throwable::class.java,
                     GuavaFunc { err -> func(err!!.cause ?: err) },
                     executor)

inline fun <T> ListenableFuture<T>.recoverWith(executor: Executor = ForkJoinExecutor,
                                               crossinline func: (Throwable) -> ListenableFuture<T>): ListenableFuture<T> =
    Futures.catchingAsync(this,
                          Throwable::class.java,
                          AsyncFunction { err -> func(err!!.cause ?: err) },
                          executor)

inline fun <T, reified E: Throwable> ListenableFuture<T>.mapError(executor: Executor = ForkJoinExecutor,
                                                                  crossinline func: (E) -> Throwable): ListenableFuture<T> =
    Futures.catching(this, E::class.java, GuavaFunc { throw func(it!!) }, executor)

fun <T> ListenableFuture<T>.fallback(executor: Executor = ForkJoinExecutor,
                                     func: () -> T): ListenableFuture<T> =
    recover(executor) { func() }

inline fun <T> ListenableFuture<T>.fallbackTo(executor: Executor = ForkJoinExecutor,
                                              crossinline func: () -> ListenableFuture<T>): ListenableFuture<T> =
    recoverWith(executor) { func() }

inline fun <T> failureCallback(crossinline failureHandler: (Throwable) -> Unit = {}): FutureCallback<T> =
    object: FutureCallback<T> {
        override fun onSuccess(result: T?) {
            // Nothing to do
        }

        override fun onFailure(t: Throwable) {
            failureHandler(t)
        }
    }

inline fun <T> successCallback(crossinline successHandler: (T) -> Unit): FutureCallback<T> =
    object: FutureCallback<T> {
        override fun onSuccess(result: T?) {
            successHandler(result!!)
        }

        override fun onFailure(t: Throwable) {
            // Nothing to do
        }
    }

inline fun <T> completeCallback(crossinline failureHandler: (Throwable) -> Unit = {},
                                crossinline successHandler: (T) -> Unit = {}): FutureCallback<T> =
    object: FutureCallback<T> {
        override fun onSuccess(result: T?) {
            successHandler(result!!)
        }

        override fun onFailure(t: Throwable) {
            failureHandler(t)
        }
    }

/**
 * NOTE: Callback 시에는 [ForkJoinExecutor] 를 쓰면 안되고, [DirectExecutor]를 사용해야만 제대로 작동됩니다.
 */
inline fun <T> ListenableFuture<T>.onFailure(executor: Executor = DirectExecutor,
                                             crossinline func: (Throwable) -> Unit): ListenableFuture<T> = apply {
    Futures.addCallback(this,
                        failureCallback { func(it) },
                        executor)
}

/**
 * NOTE: Callback 시에는 [ForkJoinExecutor] 를 쓰면 안되고, [DirectExecutor]를 사용해야만 제대로 작동됩니다.
 */
inline fun <T> ListenableFuture<T>.onSuccess(executor: Executor = DirectExecutor,
                                             crossinline func: (T) -> Unit): ListenableFuture<T> = apply {
    Futures.addCallback(this,
                        successCallback { func(it) },
                        executor)
}

/**
 * NOTE: Callback 시에는 [ForkJoinExecutor] 를 쓰면 안되고, [DirectExecutor]를 사용해야만 제대로 작동됩니다.
 */
inline fun <T> ListenableFuture<T>.onComplete(executor: Executor = DirectExecutor,
                                              crossinline onFailure: (Throwable) -> Unit = {},
                                              crossinline onSuccess: (T) -> Unit = {}): ListenableFuture<T> = apply {
    Futures.addCallback(this,
                        completeCallback(onFailure, onSuccess),
                        executor)
}


fun <T, R> Iterable<ListenableFuture<T>>.futureFold(initial: R,
                                                    executor: Executor = ForkJoinExecutor,
                                                    operation: (R, T) -> R): ListenableFuture<R> =
    this.iterator().futureFold(initial, executor, operation)

fun <T, R> Iterator<ListenableFuture<T>>.futureFold(initial: R,
                                                    executor: Executor = ForkJoinExecutor,
                                                    operation: (R, T) -> R): ListenableFuture<R> =
    if (!hasNext()) initial.asListenableFuture()
    else next().flatMap(executor) { futureFold(operation(initial, it), executor, operation) }

fun <T> Iterable<ListenableFuture<T>>.futureReduce(executor: Executor = ForkJoinExecutor,
                                                   reducer: (T, T) -> T): ListenableFuture<T> {
    val iter = iterator()
    return if (!iter.hasNext()) throw NoSuchElementException("Empty collection can't be reduced")
    else iter.next().flatMap(executor) { iter.futureFold(it, executor, reducer) }
}

fun <T, R> Iterable<ListenableFuture<T>>.futureMap(executor: Executor = ForkJoinExecutor,
                                                   mapper: (T) -> R): ListenableFuture<List<R>> {
    val initial = mutableListOf<R>().asListenableFuture()

    return fold(initial) { flr, ft ->
        flr.zip(ft, executor) { r, t -> r.add(mapper(t)); r }
    }.map(executor) { it.toList() }
}

fun <T> Iterable<ListenableFuture<T>>.futureIdentity(executor: Executor = ForkJoinExecutor): ListenableFuture<List<T>> =
    futureMap(executor) { it }
