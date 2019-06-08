package io.github.debop.connector

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

/**
 * Connector
 * @author debop (Sunghyouk Bae)
 */
interface Connector {

    fun failure(): String

    fun success(): String

    fun ignoreException(): String

    fun fluxFailure(): Flux<String>

    fun fluxSuccess(): Flux<String>

    fun monoFailure(): Mono<String>

    fun monoSuccess(): Mono<String>

    fun failureWithFallback(): String

    fun futureFailure(): CompletableFuture<String>

    fun futureSuccess(): CompletableFuture<String>
}