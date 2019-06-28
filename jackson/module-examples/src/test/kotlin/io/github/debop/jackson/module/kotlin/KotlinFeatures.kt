package io.github.debop.jackson.module.kotlin

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

/**
 * KotlinFeatures
 *
 * @author debop
 * @since 19. 6. 28
 */
class KotlinFeatures {

    companion object: KLogging()

    private val mapper = jacksonObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, false)

    private data class ClassWithPair(val name: Pair<String, String>, val age: Int)

    @Test
    fun `convert Pair`() {

        val expected = """{"name":{"first":"John","second":"Smith"},"age":30}"""
        val input = ClassWithPair(Pair("John", "Smith"), 30)

        val json = mapper.writeValueAsString(input)
        logger.debug { "json=$json" }
        json shouldEqual expected

        val output = mapper.readValue<ClassWithPair>(json)
        output shouldEqual input
    }
}