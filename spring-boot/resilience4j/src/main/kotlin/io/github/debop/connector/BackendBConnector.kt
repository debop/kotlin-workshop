package io.github.debop.connector

import io.github.debop.connector.BackendBConnector.Companion.CONNECTOR_B_NAME
import io.github.debop.exception.BusinessException
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpServerErrorException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.IOException
import java.util.concurrent.CompletableFuture

/**
 * BackendBConnector
 * @author debop (Sunghyouk Bae)
 */
@Retry(name = CONNECTOR_B_NAME)
@CircuitBreaker(name = CONNECTOR_B_NAME)
@RateLimiter(name = CONNECTOR_B_NAME)
@Component(value = CONNECTOR_B_NAME + "Connector")
class BackendBConnector : Connector {

    companion object {
        const val CONNECTOR_B_NAME = "backendB"
    }

    @Bulkhead(name = CONNECTOR_B_NAME)
    override fun failure(): String {
        throw HttpServerErrorException(INTERNAL_SERVER_ERROR, "This is a remote exception")
    }

    @Bulkhead(name = CONNECTOR_B_NAME)
    override fun success(): String {
        return "Hello World from backend B"
    }

    override fun ignoreException(): String {
        throw BusinessException("This exception is ignored by the CircuitBreaker of $CONNECTOR_B_NAME")
    }

    @Bulkhead(name = CONNECTOR_B_NAME)
    override fun fluxFailure(): Flux<String> {
        return Flux.error<String> { IOException("BAM!") }
    }

    @Bulkhead(name = CONNECTOR_B_NAME)
    override fun fluxSuccess(): Flux<String> {
        return Flux.just("Hello", "World")
    }

    @Bulkhead(name = CONNECTOR_B_NAME)
    override fun monoFailure(): Mono<String> {
        return Mono.error<String> { IOException("BAM!") }
    }

    @Bulkhead(name = CONNECTOR_B_NAME)
    override fun monoSuccess(): Mono<String> {
        return Mono.just("Hello World from backend B")
    }

    @CircuitBreaker(name = CONNECTOR_B_NAME, fallbackMethod = "fallback")
    override fun failureWithFallback(): String {
        return failure()
    }

    @Bulkhead(name = CONNECTOR_B_NAME, type = Bulkhead.Type.THREADPOOL)
    override fun futureFailure(): CompletableFuture<String> {
        val future = CompletableFuture<String>()
        future.completeExceptionally(IOException("BAM!"))
        return future
    }

    @Bulkhead(name = CONNECTOR_B_NAME, type = Bulkhead.Type.THREADPOOL)
    override fun futureSuccess(): CompletableFuture<String> {
        return CompletableFuture.completedFuture("Hello world from backend B")
    }

    private fun fallback(ex: Throwable): String {
        return "Recovered Throwable: " + ex.message
    }

    private fun fallback(ex: HttpServerErrorException): String {
        return "Recovered HttpServerErrorException: " + ex.message
    }
}