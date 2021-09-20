package io.github.debop.kotlin.workshop.examples.basic.step1

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * Suspendable main method
 */
suspend fun main() = coroutineScope {
    val jobs = List(100_000) {
        launch {
            delay(1000)
            print(".")
        }
    }
    jobs.joinAll()
}
