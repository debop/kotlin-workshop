package io.github.debop.service

import io.github.debop.connector.Connector
import io.github.debop.service.BusinessAService.Companion.SERVICE_NAME
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

/**
 * BusinessAService
 * @author debop (Sunghyouk Bae)
 */
@Service(value = SERVICE_NAME)
class BusinessAService(@Qualifier("backendAConnector") val backendAConnector: Connector) : BusinessService {

    companion object {
        const val SERVICE_NAME = "businessAService"
    }

    override fun failure(): String = backendAConnector.failure()

    override fun success(): String = backendAConnector.success()

    override fun ignore(): String = backendAConnector.ignoreException()

    override fun fluxFailure(): Flux<String> = backendAConnector.fluxFailure()

    override fun fluxSuccess(): Flux<String> = backendAConnector.fluxSuccess()

    override fun monoFailure(): Mono<String> = backendAConnector.monoFailure()

    override fun monoSuccess(): Mono<String> = backendAConnector.monoSuccess()

    override fun failureWithFallback(): String = backendAConnector.failureWithFallback()

    override fun futureFailure(): CompletableFuture<String> = backendAConnector.futureFailure()

    override fun futureSuccess(): CompletableFuture<String> = backendAConnector.futureSuccess()
}