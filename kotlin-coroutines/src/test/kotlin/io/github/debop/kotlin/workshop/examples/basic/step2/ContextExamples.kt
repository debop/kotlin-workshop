package io.github.debop.kotlin.workshop.examples.basic.step2

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldNotEqual
import org.junit.jupiter.api.Test
import kotlin.coroutines.CoroutineContext

/**
 * ContextExamples
 * @author debop (Sunghyouk Bae)
 */
class ContextExamples {

    companion object : KLogging()

    private fun printLog(output: () -> String) {
        println("[${Thread.currentThread().name}]: ${output()}")
    }

    @Test
    fun `print coroutines context`() = runBlocking<Unit> {
        val ctx: CoroutineContext = coroutineContext
        logger.info { "ctx=$ctx" }

        // looks like
        // ctx=[CoroutineId(1), "coroutine#1":BlockingCoroutine{Active}@2b676543, BlockingEventLoop@2a28445d]
        // thread name =ForkJoinPool-1-worker-1 @coroutine#1
    }

    @Test
    fun `coroutines context in launch`() = runBlocking<Unit> {
        val ctx1 = coroutineContext

        val job = GlobalScope.launch {
            val ctx2 = coroutineContext
            logger.info { "ctx2=$ctx2" }
            ctx2 shouldNotEqual ctx1
        }
        job.join()
    }

    @Test
    fun `inherit coroutines context`() = runBlocking<Unit> {
        val ctx1 = coroutineContext
        printLog { "runBlocking" }
        val job = launch(coroutineContext) {
            val ctx2 = coroutineContext
            printLog { "launch" }
            ctx2 shouldNotEqual ctx1
        }
        job.join()
    }

    @Test
    fun `create new coroutine scope`() = runBlocking<Unit> {
        launch {
            delay(200)
            printLog { "2. Task from runBlocking" }
        }

        coroutineScope {
            launch {
                delay(500)
                printLog { "3. Task from nested runBlocking" }
            }

            delay(100)
            printLog { "1. Task from coroutine scope" }
        }

        printLog { "4. Coroutine scope is over" }
    }

    suspend fun doWorld() {
        delay(1000)
        println("World!")
    }

    @Test
    fun `call suspend method`() = runBlocking<Unit> {
        launch { doWorld() }
        print("Hello, ")
    }
}