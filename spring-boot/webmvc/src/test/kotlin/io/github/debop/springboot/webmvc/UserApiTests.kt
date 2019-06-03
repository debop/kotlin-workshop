package io.github.debop.springboot.webmvc

import io.github.debop.springboot.webmvc.domain.model.User
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserApiTests(@Autowired private val restTemplate: TestRestTemplate) {

    companion object : KLogging()

    @Test
    fun `find all users and parse`() {
        val users = restTemplate.getForObject<List<User>>("/api/user/")
        users.shouldNotBeNull()
        users.size shouldEqualTo 11
    }

    @Test
    fun `verify findOne API`() {
        val userId = "violetagg"
        val expected = User(login = "violetagg",
                            firstname = "Violeta",
                            lastname = "Georgieva",
                            description = "All views are my own!")

        val user = restTemplate.getForObject<User>("/api/user/$userId")
        user shouldEqual expected
    }
}