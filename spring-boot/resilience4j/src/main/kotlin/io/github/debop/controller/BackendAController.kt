package io.github.debop.controller

import io.github.debop.controller.BackendAController.Companion.ROOT_PATH
import io.github.debop.service.BusinessService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * BackendAController
 * @author debop (Sunghyouk Bae)
 */
@RestController
@RequestMapping(value = ["/$ROOT_PATH"])
class BackendAController(@Qualifier("businessAService") val businessAService: BusinessService) {

    companion object {
        const val ROOT_PATH = "backendA"
    }

    @GetMapping("/failure")
    fun failure(): String = businessAService.failure()

    @GetMapping("/success")
    fun success(): String = businessAService.success()

    @GetMapping("/ignore")
    fun ignore() = businessAService.ignore()

    @GetMapping("/monoFailure")
    fun monoFailure() = businessAService.monoFailure()

    @GetMapping("/monoSuccess")
    fun monoSuccess() = businessAService.monoSuccess()

    @GetMapping("/fluxFailure")
    fun fluxFailure() = businessAService.fluxFailure()

    @GetMapping("/fluxSuccess")
    fun fluxSuccess() = businessAService.fluxSuccess()

    @GetMapping("/fallback")
    fun failureWithFallback() = businessAService.failureWithFallback()

    @GetMapping("/futureFailure")
    fun futureFailure() = businessAService.futureFailure()

    @GetMapping("/futureSuccess")
    fun futureSuccess() = businessAService.futureSuccess()
}