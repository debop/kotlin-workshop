package io.github.debop.kotlin.workshop.lazy

import java.util.concurrent.CompletableFuture

/**
 * FutureValue
 * @author debop (Sunghyouk Bae)
 */
class FutureValue<T: Any>(private inline val supplier: () -> T) {

    private val future = CompletableFuture.supplyAsync { supplier.invoke() }

    val value: T by lazy { future.get() }

    val isDone: Boolean get() = future.isDone
    val isCancelled: Boolean get() = future.isCancelled
    val isCompletedExceptionally: Boolean get() = future.isCompletedExceptionally
}