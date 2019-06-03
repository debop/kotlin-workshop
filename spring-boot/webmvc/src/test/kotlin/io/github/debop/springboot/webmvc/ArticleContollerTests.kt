package io.github.debop.springboot.webmvc

import io.github.debop.springboot.webmvc.domain.model.Article
import mu.KLogging
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.getForObject
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@TestMethodOrder(OrderAnnotation::class)
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ArticleContollerTests(@Autowired private val restTemplate: TestRestTemplate) {

    companion object : KLogging()

    @Test
    @Order(1)
    fun `find all articles`() {
        val articles = restTemplate.getForObject<List<Article>>("/api/article/")
        articles!!.size shouldEqualTo 3
    }

    @Test
    @Order(2)
    fun `fineOne article`() {
        val slug = "spring-framework-5-0-goes-ga"

        val article = restTemplate.getForObject<Article>("/api/article/$slug")
        article.shouldNotBeNull()

        article.slug shouldEqual slug
        article.title shouldEqual "Spring Framework 5.0 goes GA"
        article.author.login shouldEqual "springjuergen"
        article.addedAt shouldEqual LocalDateTime.of(2017, 9, 28, 11, 30)

        article.headline shouldContain ("[repo.spring.io](https://repo.spring.io)")
    }

    @Test
    @Order(3)
    fun `findOne article with markdown converter`() {
        val slug = "spring-framework-5-0-goes-ga"

        val article = restTemplate.getForObject<Article>("/api/article/$slug?converter=markdown")

        article.shouldNotBeNull()

        article.slug shouldEqual slug
        article.title shouldEqual "Spring Framework 5.0 goes GA"
        article.author.login shouldEqual "springjuergen"
        article.addedAt shouldEqual LocalDateTime.of(2017, 9, 28, 11, 30)

        article.headline shouldNotContain ("[repo.spring.io](https://repo.spring.io)")
        article.headline shouldContain ("<a href=\"https://repo.spring.io\">repo.spring.io</a>")
    }

    @Test
    @Order(4)
    fun `findOne article with invalid converter`() {
        val slug = "spring-framework-5-0-goes-ga"

        val entity = restTemplate.getForEntity<String>("/api/article/$slug?converter=foo")
        entity.statusCode shouldEqual HttpStatus.INTERNAL_SERVER_ERROR
        entity.body!! shouldContain "Only markdown converter is supported"
    }
}