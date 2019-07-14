package io.github.debop.futures.guava

import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * [ForkJoiPool]을 사용하는 [ExecutorService]
 */
object ForkJoinExecutor: ExecutorService by ForkJoinPool.commonPool()

/**
 * 같은 Thread context에서 수행하는 [ExecutorService]
 */
object DirectExecutor: ExecutorService {

    override fun execute(command: Runnable) {
        command.run()
    }

    override fun shutdown() {
    }

    override fun <T: Any?> submit(task: Callable<T>): Future<T> {
        throw NotImplementedError()
    }

    override fun <T: Any?> submit(task: Runnable, result: T): Future<T> {
        throw NotImplementedError()
    }

    override fun submit(task: Runnable): Future<*> {
        throw NotImplementedError()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        throw NotImplementedError()
    }

    override fun isShutdown(): Boolean {
        throw NotImplementedError()
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        throw NotImplementedError()
    }

    override fun <T: Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T {
        throw NotImplementedError()
    }

    override fun <T: Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T {
        throw NotImplementedError()
    }

    override fun isTerminated(): Boolean {
        throw NotImplementedError()
    }

    override fun <T: Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> {
        throw NotImplementedError()
    }

    override fun <T: Any?> invokeAll(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): MutableList<Future<T>> {
        throw NotImplementedError()
    }

}