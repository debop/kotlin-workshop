package io.github.debop.service

import io.github.debop.connector.Connector
import io.github.debop.service.BusinessBService.Companion.SERVICE_B_NAME
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.CompletableFuture

/**
 * BusinessBService
 * @author debop (Sunghyouk Bae)
 */
@Service(value = SERVICE_B_NAME)
class BusinessBService(@Qualifier("backendBConnector") val backendBConnector: Connector) : BusinessService {

    companion object {
        const val SERVICE_B_NAME = "businessBService"
    }

    override fun failure(): String = backendBConnector.failure()

    override fun success(): String = backendBConnector.success()

    override fun ignore(): String = backendBConnector.ignoreException()

    override fun fluxFailure(): Flux<String> = backendBConnector.fluxFailure()

    override fun fluxSuccess(): Flux<String> = backendBConnector.fluxSuccess()

    override fun monoFailure(): Mono<String> = backendBConnector.monoFailure()

    override fun monoSuccess(): Mono<String> = backendBConnector.monoSuccess()

    override fun failureWithFallback(): String = backendBConnector.failureWithFallback()

    override fun futureFailure(): CompletableFuture<String> = backendBConnector.futureFailure()

    override fun futureSuccess(): CompletableFuture<String> = backendBConnector.futureSuccess()
}