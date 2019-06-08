package io.github.debop.service

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

/**
 * BusinessService
 * @author debop (Sunghyouk Bae)
 */
interface BusinessService {

    fun failure(): String

    fun success(): String

    fun ignore(): String

    fun fluxFailure(): Flux<String>

    fun fluxSuccess(): Flux<String>

    fun monoFailure(): Mono<String>

    fun monoSuccess(): Mono<String>

    fun failureWithFallback(): String

    fun futureFailure(): CompletableFuture<String>

    fun futureSuccess(): CompletableFuture<String>

}