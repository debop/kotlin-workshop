package io.github.debop.kotlin.tests.mockk.examples.baeldung

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * [MockK: A Mocking Library for Kotlin](https://www.baeldung.com/kotlin-mockk)
 */
class BaeldungExample {

    class TestableService {

        fun getDataFromDb(testParamter: String): String {
            // query database and return matching value
            return testParamter
        }

        fun doSomethingElse(testParamter: String): String {
            return "I don't want to `$testParamter`"
        }
    }

    @Test
    fun `when calling mocked method then correctly verified`() {
        // given
        val service = mockk<TestableService>()
        every { service.getDataFromDb("Expected param") } returns "Expected Output"

        // when
        val result = service.getDataFromDb("Expected param")

        // then
        verify { service.getDataFromDb("Expected param") }
        result shouldEqual "Expected Output"
    }


    class InjectTestService {
        lateinit var service1: TestableService
        lateinit var service2: TestableService

        fun invokeService1(): String {
            return service1.getDataFromDb("Test Param")
        }
    }

    @MockK
    lateinit var service1: TestableService

    @MockK
    lateinit var service2: TestableService

    @InjectMockKs
    var objectUnderTest = InjectTestService()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `injected mock verified`() {

        every { service1.getDataFromDb(any()) } returns "Good output"

        objectUnderTest.invokeService1() shouldEqual "Good output"

        verify {
            service1.getDataFromDb(any())
        }
    }


    @Test
    fun `given service spy`() {
        // given
        val service = spyk<TestableService>()
        every { service.getDataFromDb(any()) } returns "Mocked Output"

        // then
        service.getDataFromDb("Any Param") shouldEqual "Mocked Output"

        service.doSomethingElse("Any Param") shouldEqual "I don't want to `Any Param`"
    }

}