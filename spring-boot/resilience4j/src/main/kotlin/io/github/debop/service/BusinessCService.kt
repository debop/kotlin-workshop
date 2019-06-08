package io.github.debop.service

import io.github.debop.connector.Connector
import io.github.debop.service.BusinessCService.Companion.SERVICE_C_NAME
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

/**
 * BusinessCService
 * @author debop (Sunghyouk Bae)
 */
@Service(value = SERVICE_C_NAME)
class BusinessCService(@Qualifier("backendCConnector") val backendCConnector: Connector) : BusinessService {

    companion object {
        const val SERVICE_C_NAME = "businessCService"
    }

    override fun failure(): String = backendCConnector.failure()

    override fun success(): String = backendCConnector.success()

    override fun ignore(): String = backendCConnector.ignoreException()

    override fun fluxFailure(): Flux<String> = backendCConnector.fluxFailure()

    override fun fluxSuccess(): Flux<String> = backendCConnector.fluxSuccess()

    override fun monoFailure(): Mono<String> = backendCConnector.monoFailure()

    override fun monoSuccess(): Mono<String> = backendCConnector.monoSuccess()

    override fun failureWithFallback(): String = backendCConnector.failureWithFallback()

    override fun futureFailure(): CompletableFuture<String> = backendCConnector.futureFailure()

    override fun futureSuccess(): CompletableFuture<String> = backendCConnector.futureSuccess()
}