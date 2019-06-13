package io.github.debop.kotlin.tests.mockk.examples

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * MockkAnnotationExample
 *
 * @author debop
 * @since 19. 6. 13
 */
class MockkAnnotationExample {

    interface Dependency1 {
        val value1: Int
        fun call(): Dependency1
        fun add(a: Int): Int
    }

    interface Dependency2 {
        val value2: String
    }

    class Dependency3 {
        fun sub(a: Int): Int = a
    }

    class SystemUnderTest(val doc1: Dependency1, val doc2: Dependency2, val doc3: Dependency3) {
        fun calculate(): Int = doc3.sub(doc1.call().add(10) + doc2.value2.toInt())
    }

    @MockK
    lateinit var doc1: Dependency1

    @RelaxedMockK
    lateinit var doc2: Dependency2

    @SpyK
    var doc3: Dependency3 = Dependency3()

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `mockk annotation example`() {

        every { doc1.call().add(any()) } returns 5
        every { doc2.value2 } returns "6"
        every { doc3.sub(any()) } returns 7

        val sut = SystemUnderTest(doc1, doc2, doc3)

        sut.calculate() shouldEqualTo 7
    }
}