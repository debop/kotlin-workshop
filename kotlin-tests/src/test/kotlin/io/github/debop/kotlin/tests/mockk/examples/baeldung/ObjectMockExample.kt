package io.github.debop.kotlin.tests.mockk.examples.baeldung

import io.mockk.every
import io.mockk.mockkObject
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

/**
 * ObjectMockExample
 *
 * @author debop
 * @since 19. 6. 13
 */
class ObjectMockExample {

    object TestableService {
        fun getDataFromDb(testParameter: String): String = testParameter
    }

    @Test
    fun `object mocking`() {
        // given
        mockkObject(TestableService)

        // when calling not mocked method
        TestableService.getDataFromDb("Any Param") shouldBeEqualTo "Any Param"

        // when calling mocked method
        every { TestableService.getDataFromDb(any()) } returns "Mocked Output"
        TestableService.getDataFromDb("Any Param") shouldBeEqualTo "Mocked Output"
    }
}