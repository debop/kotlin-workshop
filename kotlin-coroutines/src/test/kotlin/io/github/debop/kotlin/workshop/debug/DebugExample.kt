package io.github.debop.kotlin.workshop.debug

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.Test

suspend fun computeOne(): String {
    delay(5000)
    return "4"
}

suspend fun computeTwo(): String {
    delay(5000)
    return "2"
}

suspend fun combineResults(one: Deferred<String>, two: Deferred<String>): String =
    one.await() + two.await()

suspend fun computeValue(): String = coroutineScope {
    val one = async { computeOne() }
    val two = async { computeTwo() }
    combineResults(one, two)
}

@Suppress("EXPERIMENTAL_API_USAGE")
class DebugExample {

    companion object: KLogging()

    @Test
    fun `DebugProbes example`() = runBlocking<Unit> {
        DebugProbes.install()

        val deferred = async { computeValue() }

        delay(1000)

        // Dump running coroutines
        DebugProbes.dumpCoroutines()
        println("\nDumping only defered")
        DebugProbes.printJob(deferred)
    }
}