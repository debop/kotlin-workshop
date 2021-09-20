package io.github.debop.futures.jdk8

import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.function.BiConsumer
import java.util.function.Function
import java.util.function.Supplier


inline fun <T> futureOf(executor: Executor = ForkJoinExecutor, crossinline block: () -> T): CompletableFuture<T> =
    CompletableFuture.supplyAsync(Supplier { block.invoke() }, executor)

inline fun <T> immediateFuture(crossinline block: () -> T): CompletableFuture<T> =
    futureOf(DirectExecutor, block)

fun <T> T.asCompletableFuture(): CompletableFuture<T> = CompletableFuture.completedFuture(this)

fun <T> Throwable.asCompletableFuture(): CompletableFuture<T> = CompletableFuture<T>().apply {
    completeExceptionally(this@asCompletableFuture)
}

inline fun <T, R> CompletableFuture<T>.map(
    executor: Executor = ForkJoinExecutor,
    crossinline mapper: (T) -> R,
): CompletableFuture<R> =
    thenApplyAsync(Function<T, R> { mapper(it) }, executor)

inline fun <T, R> CompletableFuture<T>.flatMap(
    executor: Executor = ForkJoinExecutor,
    crossinline flatMapper: (T) -> CompletableFuture<R>,
): CompletableFuture<R> =
    thenComposeAsync(Function { flatMapper(it) }, executor)

fun <T> CompletableFuture<CompletableFuture<T>>.flatten(): CompletableFuture<T> = flatMap { it }

inline fun <T> CompletableFuture<T>.filter(
    executor: Executor = ForkJoinExecutor,
    crossinline predicate: (T) -> Boolean,
): CompletableFuture<T> =
    flatMap(executor) {
        if (predicate(it)) this
        else NoSuchElementException("CompletableFuture.filter predicate is not satisfied").asCompletableFuture()
    }

inline fun <T, U, R> CompletableFuture<T>.zip(
    other: CompletableFuture<U>,
    executor: Executor = ForkJoinExecutor,
    crossinline combiner: (T, U) -> R,
): CompletableFuture<R> =
    thenCombineAsync(other, { t, u -> combiner(t, u) }, executor)

fun <T, U> CompletableFuture<T>.zip(
    other: CompletableFuture<U>,
    executor: Executor = ForkJoinExecutor,
): CompletableFuture<Pair<T, U>> =
    zip(other, executor) { t, u -> t to u }


inline fun <T> CompletableFuture<T>.recover(crossinline func: (Throwable) -> T): CompletableFuture<T> =
    exceptionally { func(it.cause ?: it) }

inline fun <T> CompletableFuture<T>.recoverWith(
    executor: Executor = ForkJoinExecutor,
    crossinline func: (Throwable) -> CompletableFuture<T>,
): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    onComplete(
        executor,
        {
            func(it)
                .onComplete(executor,
                            { error -> future.completeExceptionally(error) },
                            { result -> future.complete(result) })
        },
        { future.complete(it) }
    )
    return future
}

inline fun <T, reified E: Throwable> CompletableFuture<T>.mapError(crossinline mapper: (E) -> Throwable): CompletableFuture<T> =
    exceptionally {
        val error = it.cause ?: it
        if (error is E) {
            throw mapper(error)
        } else {
            throw error
        }
    }

inline fun <T> CompletableFuture<T>.fallback(crossinline func: () -> T): CompletableFuture<T> =
    recover { func() }

inline fun <T> CompletableFuture<T>.fallbackTo(
    executor: Executor = ForkJoinExecutor,
    crossinline func: () -> CompletableFuture<T>,
): CompletableFuture<T> =
    recoverWith(executor) { func() }


inline fun <T> CompletableFuture<T>.onFailure(
    executor: Executor = ForkJoinExecutor,
    crossinline block: (Throwable) -> Unit,
): CompletableFuture<T> =
    whenCompleteAsync(BiConsumer { _, error: Throwable? ->
        error?.let { block(it.cause ?: it) }
    }, executor)

inline fun <T> CompletableFuture<T>.onSuccess(
    executor: Executor = ForkJoinExecutor,
    crossinline block: (T) -> Unit,
): CompletableFuture<T> =
    whenCompleteAsync(BiConsumer { result: T, _ ->
        block(result)
    }, executor)

inline fun <T> CompletableFuture<T>.onComplete(
    executor: Executor = ForkJoinExecutor,
    crossinline onFailure: (Throwable) -> Unit,
    crossinline onSuccess: (T) -> Unit,
): CompletableFuture<T> {
    return whenCompleteAsync(BiConsumer { result: T, error: Throwable? ->
        if (error != null) {
            onFailure(error.cause ?: error)
        } else {
            onSuccess(result)
        }
    }, executor)
}

fun <T> Iterable<CompletableFuture<T>>.futureFirst(executor: Executor = ForkJoinExecutor): CompletableFuture<T> {
    val future = CompletableFuture<T>()
    val onCompleteFirst: (CompletableFuture<T>) -> Unit = {
        it.onComplete(executor,
                      { error -> if (!future.isDone) future.completeExceptionally(error) },
                      { result -> if (!future.isDone) future.complete(result) })
    }
    this.forEach(onCompleteFirst)
    return future
}

fun <T> Iterable<CompletableFuture<T>>.futureFlatten(executor: Executor = ForkJoinExecutor): CompletableFuture<List<T>> {
    return futureMap(executor) { it }
}

fun <T, R> Iterable<CompletableFuture<T>>.futureMap(
    executor: Executor = ForkJoinExecutor,
    mapper: (T) -> R,
): CompletableFuture<List<R>> {
    val initial = mutableListOf<R>().asCompletableFuture()

    return fold(initial) { flr, ft ->
        flr.zip(ft, executor) { r, t -> r.add(mapper(t)); r }
    }
        .map(executor) { it.toList() }
}

fun <T> Iterable<CompletableFuture<T>>.futureSuccessful(executor: Executor = ForkJoinExecutor): CompletableFuture<List<T>> {
    val initial = mutableListOf<T>().asCompletableFuture()

    return fold(initial) { flr, ft ->
        flr.flatMap(executor) { r ->
            ft
                .map(executor) { Optional.of(it) }
                .recover { Optional.empty() }
                .map(executor) {
                    if (it.isPresent) {
                        r.add(it.get())
                    }
                    r
                }
        }
    }.map(executor) { it.toList() }
}

fun <T, R> Iterable<CompletableFuture<T>>.futureFold(
    initial: R,
    executor: Executor = ForkJoinExecutor,
    operation: (R, T) -> R,
): CompletableFuture<R> =
    this.iterator().futureFold(initial, executor, operation)

fun <T, R> Iterator<CompletableFuture<T>>.futureFold(
    initial: R,
    executor: Executor = ForkJoinExecutor,
    operation: (R, T) -> R,
): CompletableFuture<R> =
    if (!hasNext()) initial.asCompletableFuture()
    else next().flatMap(executor) { futureFold(operation(initial, it), executor, operation) }

fun <T> Iterable<CompletableFuture<T>>.futureReduce(
    executor: Executor = ForkJoinExecutor,
    reducer: (T, T) -> T,
): CompletableFuture<T> {
    val iter = iterator()

    return if (!iter.hasNext()) throw NoSuchElementException("Empty collection can't be reduced.")
    else iter.next().flatMap(executor) { iter.futureFold(it, executor, reducer) }
}

