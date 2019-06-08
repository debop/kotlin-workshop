package io.github.debop.controller

import io.github.debop.controller.BackendBController.Companion.ROOT_PATH
import io.github.debop.service.BusinessService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * BackendBController
 * @author debop (Sunghyouk Bae)
 */
@RestController
@RequestMapping(value = ["/$ROOT_PATH"])
class BackendBController(@Qualifier("businessBService") val businessBService: BusinessService) {

    companion object {
        const val ROOT_PATH = "backendB"
    }

    @GetMapping("/failure")
    fun failure(): String = businessBService.failure()

    @GetMapping("/success")
    fun success(): String = businessBService.success()

    @GetMapping("/ignore")
    fun ignore() = businessBService.ignore()

    @GetMapping("/monoFailure")
    fun monoFailure() = businessBService.monoFailure()

    @GetMapping("/monoSuccess")
    fun monoSuccess() = businessBService.monoSuccess()

    @GetMapping("/fluxFailure")
    fun fluxFailure() = businessBService.fluxFailure()

    @GetMapping("/fluxSuccess")
    fun fluxSuccess() = businessBService.fluxSuccess()

    @GetMapping("/fallback")
    fun failureWithFallback() = businessBService.failureWithFallback()

    @GetMapping("/futureFailure")
    fun futureFailure() = businessBService.futureFailure()

    @GetMapping("/futureSuccess")
    fun futureSuccess() = businessBService.futureSuccess()
}