package io.github.debop.springboot.basic.json

import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester

@JsonTest
class JacksonTests {

    companion object: KLogging()

    data class Book(val title: String, val isbn: String? = null)

    @Autowired
    private lateinit var json: JacksonTester<Book>

    @Test
    fun `loading JacksonTester`() {
        json.shouldNotBeNull()
    }

    @Test
    fun `json convert test`() {

        val book = Book("Spring Boot", "12345")
        val expectedJson = "{\"title\":\"Spring Boot\",\"isbn\":\"12345\"}"

        val jsonContent = json.write(book)
        assertThat(jsonContent).hasJsonPathStringValue("title")
        assertThat(jsonContent).extractingJsonPathStringValue("title").isEqualTo("Spring Boot")

        logger.debug { "book=$book" }
        logger.debug { "json=${jsonContent.json}" }
        jsonContent.json shouldEqual expectedJson

        val converted = json.parseObject(jsonContent.json)
        converted.shouldNotBeNull()
        converted shouldEqual book
    }
}