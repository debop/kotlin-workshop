package io.github.debop.kotlin.workshop.debug

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class RecoveryExample {

    object PublicApiImplementation: CoroutineScope by CoroutineScope(CoroutineName("Example")) {

        private fun doWork(): Int {
            error("Internal invariant failed")
        }

        private fun asynchronousWork(): Int {
            return doWork() + 1
        }

        suspend fun awaitAsynchronousWorkInMainThread() {
            val task = async(Dispatchers.Default) {
                asynchronousWork()
            }
            task.await()
        }
    }

    @Test
    fun `switch debug mode on and off to see the difference`() = runBlocking<Unit> {
        PublicApiImplementation.awaitAsynchronousWorkInMainThread()
    }
}