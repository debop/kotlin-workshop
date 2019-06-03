package io.github.debop.springboot.webflux

import io.github.debop.springboot.webflux.domain.model.User
import mu.KLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import reactor.test.test

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserApiTests(@LocalServerPort port: Int) {

    companion object : KLogging()

    private val client: WebClient = WebClient.create("http://localhost:$port")

    @Test
    fun `find all users and parse`() {
        client.get().uri("/api/user/").retrieve().bodyToFlux<User>()
            .doOnNext { logger.trace { "user=$it" } }
            .test()
            .expectNextCount(11)
            .verifyComplete()
    }
}