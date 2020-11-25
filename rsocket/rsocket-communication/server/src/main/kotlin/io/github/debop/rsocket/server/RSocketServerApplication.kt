package io.github.debop.rsocket.server

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class RSocketServerApplication

fun main(vararg args: String) {
    runApplication<RSocketServerApplication>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
    }
}