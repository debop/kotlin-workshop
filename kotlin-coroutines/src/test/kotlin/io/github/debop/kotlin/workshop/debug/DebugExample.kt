package io.github.debop.kotlin.workshop.debug

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.debug.DebugProbes
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

suspend fun computeOne(): String {
    delay(500)
    return "4"
}

suspend fun computeTwo(): String {
    delay(500)
    return "2"
}

suspend fun combineResults(one: Deferred<String>, two: Deferred<String>): String =
    one.await() + two.await()

suspend fun computeValue(): String = coroutineScope {
    val one = async { computeOne() }
    val two = async { computeTwo() }
    combineResults(one, two)
}

@ExperimentalCoroutinesApi
class DebugExample {

    companion object: KLogging()

    @Test
    fun `DebugProbes example`() = runBlocking<Unit> {
        //        DebugProbes.install()
        //
        //        val deferred = async { computeValue() }
        //
        //        delay(100)
        //
        //        // Dump running coroutines
        //        DebugProbes.dumpCoroutines()
        //        println("\nDumping only defered")
        //        DebugProbes.printJob(deferred)

        val dump = withDebugProbe {
            val deferred = async { computeValue() }

            delay(100)
            deferred
        }
        println(dump)
    }
}

@ExperimentalCoroutinesApi
suspend inline fun withDebugProbe(action: suspend () -> Job): String {
    DebugProbes.install()

    return try {
        val job = action()
        ByteArrayOutputStream().use { bos ->
            val out = PrintStream(bos)
            // dump running coroutines
            DebugProbes.dumpCoroutines(out)
            out.println()
            out.println()
            DebugProbes.printJob(job, out)
            String(bos.toByteArray(), Charsets.UTF_8)
        }
    } finally {
        DebugProbes.uninstall()
    }
}