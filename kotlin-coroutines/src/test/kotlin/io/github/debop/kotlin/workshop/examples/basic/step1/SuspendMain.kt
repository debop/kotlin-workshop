package io.github.debop.kotlin.workshop.examples.basic.step1

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

/**
 * Suspendable main method
 */
suspend fun main() {
    val jobs = List(100_000) {
        GlobalScope.launch {
            delay(1000)
            print(".")
        }
    }
    jobs.joinAll()
}