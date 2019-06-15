package io.github.debop.kotlin.workshop

import mu.KLogging
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@TestInstance(Lifecycle.PER_CLASS) // see resources/junit-platform.properties
class HelloworldTest {

    companion object : KLogging()

    @BeforeAll
    fun beforeAll() {
        logger.debug { "BeforeAll - Call before running all test methods" }
    }

    @BeforeEach
    fun beforeEach() {
        logger.debug { "BeforeEach - Call before running each test method" }
    }

    @AfterEach
    fun afterEach() {
        logger.debug { "AfterEach - Call before running each test method" }
    }

    @AfterAll
    fun afterAll() {
        logger.debug { "AfterAll - Call before running all test methods" }
    }

    @Test
    fun `my first test using junit5`() {
        logger.debug { "My first time test method using JUnit5" }
    }
}