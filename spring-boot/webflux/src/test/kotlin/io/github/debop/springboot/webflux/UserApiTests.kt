package io.github.debop.springboot.webflux

import io.github.debop.springboot.webflux.domain.model.User
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.test.test

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserApiTests(@LocalServerPort port: Int) {

    companion object : KLogging()

    // TODO Migrate to WebTestClient when https://youtrack.jetbrains.com/issue/KT-5464 will be fixed
    private val client: WebClient = WebClient.create("http://localhost:$port")

    @Test
    fun `find all users and parse`() {
        client.get().uri("/api/user/").retrieve().bodyToFlux<User>()
            .doOnNext { logger.trace { "user=$it" } }
            .test()
            .expectNextCount(11)
            .verifyComplete()
    }

    @Test
    fun `verify findOne API`() {
        val userId = "violetagg"
        val expected = User(login = "violetagg",
                            firstname = "Violeta",
                            lastname = "Georgieva",
                            description = "All views are my own!")

        client.get()
            .uri { it.path("/api/user/{userId}").build(mapOf("userId" to userId)) }
            .retrieve()
            .bodyToMono<User>()
            .doOnNext { logger.trace { "user=$it" } }
            .test()
            .consumeNextWith { user -> user shouldEqual expected }
            .verifyComplete()
    }
}