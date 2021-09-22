package io.github.debop.kotlin.workshop.examples.basic.step1

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import kotlin.concurrent.thread

class NonBlockingExample {
    companion object: KLogging()

    @Disabled("Blocking 때문에 엄청난 시간이 걸린다")
    @Test
    @Order(1)
    fun `blocking thread`() {
        val threads = List(100_000) {
            thread {
                Thread.sleep(1000)
                print(".")
            }
        }
        threads.forEach { it.join() }
    }

    @Disabled("Blocking 때문에 엄청난 시간이 걸린다")
    @Test
    @Order(2)
    fun `blocking with coroutines`() {
        runBlocking {
            val scope = CoroutineScope(Dispatchers.IO)
            val jobs = List(100_000) {
                scope.launch {
                    Thread.sleep(1000)
                    print(".")
                }
            }
            jobs.joinAll()
        }
    }

    @Test
    @Order(3)
    fun `non-blocking by delay with coroutines`() {
        runBlocking(Dispatchers.IO) {
            val jobs = List(100_000) {
                launch {
                    delay(1000)
                    print(".")
                }
            }
            jobs.joinAll()
        }
    }
}

