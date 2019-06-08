package io.github.debop.controller

import io.github.debop.controller.BackendCController.Companion.ROOT_PATH
import io.github.debop.service.BusinessService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * BackendCController
 * @author debop (Sunghyouk Bae)
 */
@RestController
@RequestMapping(value = ["/$ROOT_PATH"])
class BackendCController(@Qualifier("businessCService") val businessCService: BusinessService) {

    companion object {
        const val ROOT_PATH = "backendC"
    }

    @GetMapping("/failure")
    fun failure(): String = businessCService.failure()

    @GetMapping("/success")
    fun success(): String = businessCService.success()

    @GetMapping("/ignore")
    fun ignore() = businessCService.ignore()

    @GetMapping("/monoFailure")
    fun monoFailure() = businessCService.monoFailure()

    @GetMapping("/monoSuccess")
    fun monoSuccess() = businessCService.monoSuccess()

    @GetMapping("/fluxFailure")
    fun fluxFailure() = businessCService.fluxFailure()

    @GetMapping("/fluxSuccess")
    fun fluxSuccess() = businessCService.fluxSuccess()

    @GetMapping("/fallback")
    fun failureWithFallback() = businessCService.failureWithFallback()

    @GetMapping("/futureFailure")
    fun futureFailure() = businessCService.futureFailure()

    @GetMapping("/futureSuccess")
    fun futureSuccess() = businessCService.futureSuccess()
}