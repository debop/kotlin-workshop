package io.github.debop.springboot.routes

import io.github.debop.springboot.routes.domain.model.Article
import io.github.debop.springboot.routes.domain.model.ArticleEvent
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.TEXT_EVENT_STREAM
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.test.test
import java.time.LocalDateTime

@TestMethodOrder(OrderAnnotation::class)
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ArticleContollerTests(@LocalServerPort private val port: Int) {

    companion object : KLogging()

    // TODO Migrate to WebTestClient when https://youtrack.jetbrains.com/issue/KT-5464 will be fixed
    private val client: WebClient = WebClient.create("http://localhost:$port")

    @Test
    @Order(1)
    fun `find all articles`() {
        client.get().uri("/api/article/").retrieve().bodyToFlux<Article>()
            .doOnNext { logger.trace { "article=${it.slug}" } }
            .test()
            .expectNextCount(3)
            .verifyComplete()
    }

    @Test
    @Order(2)
    fun `fineOne article`() {
        val slug = "spring-framework-5-0-goes-ga"

        client.get().uri("/api/article/$slug").retrieve().bodyToMono<Article>()
            .doOnNext { logger.trace("article slug=${it.slug}") }
            .test()
            .consumeNextWith { article ->
                logger.trace { article }
                article.slug shouldBeEqualTo slug
                article.title shouldBeEqualTo "Spring Framework 5.0 goes GA"
                article.author shouldBeEqualTo "springjuergen"
                article.addedAt shouldBeEqualTo LocalDateTime.of(2017, 9, 28, 11, 30)

                article.headline shouldContain ("[repo.spring.io](https://repo.spring.io)")
            }
            .verifyComplete()
    }

    @Test
    @Order(3)
    fun `findOne article with markdown converter`() {
        val slug = "spring-framework-5-0-goes-ga"

        client.get().uri("/api/article/$slug?converter=markdown").retrieve().bodyToMono<Article>()
            .doOnNext { logger.trace("article slug=${it.slug}") }
            .test()
            .consumeNextWith { article ->
                logger.trace { article }
                article.slug shouldBeEqualTo slug
                article.title shouldBeEqualTo "Spring Framework 5.0 goes GA"
                article.author shouldBeEqualTo "springjuergen"
                article.addedAt shouldBeEqualTo LocalDateTime.of(2017, 9, 28, 11, 30)

                article.headline shouldNotContain ("[repo.spring.io](https://repo.spring.io)")
                article.headline shouldContain ("<a href=\"https://repo.spring.io\">repo.spring.io</a>")
            }
            .verifyComplete()
    }

    @Test
    @Order(4)
    fun `findOne article with invalid converter`() {
        val slug = "spring-framework-5-0-goes-ga"

        client.get().uri("/api/article/$slug?converter=foo").exchange()
            .test()
            .consumeNextWith {
                it.statusCode() shouldBeEqualTo HttpStatus.INTERNAL_SERVER_ERROR
            }
            .verifyComplete()
    }

    @Test
    @Order(Int.MAX_VALUE)
    fun `notification via SSE`() {

        val newArticle = Article("foo", "Foo", "foo", "foo", "mark", LocalDateTime.now())

        client.get().uri("/api/article/notifications").accept(TEXT_EVENT_STREAM).retrieve().bodyToFlux<ArticleEvent>()
            .take(1)
            .doOnSubscribe {
                client.post().uri("/api/article/").syncBody(newArticle).exchange().subscribe()
            }
            .test()
            .consumeNextWith { evt ->
                evt.slug shouldBeEqualTo newArticle.slug
                evt.title shouldBeEqualTo newArticle.title
            }
            .verifyComplete()
    }
}