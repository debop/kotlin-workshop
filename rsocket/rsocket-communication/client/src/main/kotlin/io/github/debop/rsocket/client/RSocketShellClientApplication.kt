package io.github.debop.rsocket.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration
import org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [
    ReactiveUserDetailsServiceAutoConfiguration::class,
    SecurityAutoConfiguration::class,
    ReactiveSecurityAutoConfiguration::class,
    RSocketSecurityAutoConfiguration::class,
])
class RSocketShellClientApplication

fun main(vararg args: String) {
    runApplication<RSocketShellClientApplication>(*args)
}