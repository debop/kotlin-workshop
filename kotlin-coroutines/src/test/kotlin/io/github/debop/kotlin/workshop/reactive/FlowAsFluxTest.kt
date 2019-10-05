package io.github.debop.kotlin.workshop.reactive

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.ReactorContext
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.util.context.Context

@Suppress("EXPERIMENTAL_API_USAGE")
class FlowAsFluxTest {

    companion object: KLogging()

    @Test
    fun `flow to flux - context propagation`() = runBlocking<Unit> {
        val flux = flow<String> {
            (1..4).forEach { i ->
                logger.debug { "Before emitting $i ..." }
                emit(m(i).awaitFirst())
                logger.debug { "After emitted $i ..." }
            }
        }.asFlux()
            .subscriberContext(Context.of(1, "1"))
            .subscriberContext(Context.of(2, "2", 3, "3", 4, "4"))

        var i = 0

        flux
            .subscribe { str ->
                i++
                logger.debug { "Received: $str" }
                // i.toString() shouldEqual str
            }
    }

    private fun m(i: Int): Mono<String> = mono {
        val ctx = coroutineContext[ReactorContext]?.context
        ctx?.getOrDefault(i, "noValue")
    }
}