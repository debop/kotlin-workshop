package io.github.debop.kotlin.tests.mockk.examples

import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test

/**
 * See these articles
 *
 * 1. [Mocking is not rocket science: Basics](https://blog.kotlin-academy.com/mocking-is-not-rocket-science-basics-ae55d0aadf2b)
 * 2. [Mocking is not rocket science: Expected behavior and behavior verification](https://blog.kotlin-academy.com/mocking-is-not-rocket-science-expected-behavior-and-behavior-verification-3862dd0e0f03)
 * 3. [Mocking is not rocket science: MockK features](https://blog.kotlin-academy.com/mocking-is-not-rocket-science-mockk-features-e5d55d735a98)
 */
class MockkExamples {

    data class Dependency1(val value1: Int)
    data class Dependency2(val value2: String)

    class SystemUnderTest(val dependency1: Dependency1, val dependency2: Dependency2) {
        fun calculate() = dependency1.value1 + dependency2.value2.toInt()
    }

    @Test
    fun `mock dependency objects`() {
        val doc1 = mockk<Dependency1>()
        val doc2 = mockk<Dependency2>()

        every { doc1.value1 } returns 5
        every { doc2.value2 } returns "6"

        val sut = SystemUnderTest(doc1, doc2)
        sut.calculate() shouldBeEqualTo 11

        verify(exactly = 1) {
            doc1.value1
            doc2.value2
        }
    }


    interface Service {
        fun call(a: Int): Int
        fun call(a: Int, b: Int): Int
        fun findAll(a: Int): List<Int>

        fun delete(a: Int)
    }

    @Test
    fun `argument matching`() {
        val mock = mockk<Service>()

        every { mock.call(more(5)) } returns 1
        every { mock.call(more(5), 6) } returns -1

        mock.call(6) shouldBeEqualTo 1
        mock.call(9, 6) shouldBeEqualTo -1
    }

    @Test
    fun `return multiple list`() {
        val mock = mockk<Service>()

        every { mock.findAll(5) } returns listOf(1, 2, 3)

        every { mock.findAll(10) } returns listOf(1) andThen listOf(2)   // return first argument value ( a=10 )

        mock.findAll(10) shouldContainSame listOf(1)  // first call
        mock.findAll(10) shouldContainSame listOf(2)  // second call
    }

    @Test
    fun `when method return void`() {

        val mock = mockk<Service>()

        every { mock.delete(any()) } just Runs

        mock.delete(5)

        verify {
            mock.delete(any())
        }
    }

    @Test
    fun `answer by parameters`() {
        val mock = mockk<Service>()
        clearMocks(mock)

        every { mock.call(any()) } answers { arg<Int>(0) * 2 }

        mock.call(5) shouldBeEqualTo 10
        mock.call(-2) shouldBeEqualTo -4

        verify(exactly = 1) {
            mock.call(5)
            mock.call(-2)
        }
        verifyOrder {
            mock.call(5)
            mock.call(-2)
        }
    }


    class Divider {
        fun divide(a: Int, b: Int) = a / b
    }

    @Test
    fun `capturing slot`() {

        val slot = slot<Int>()
        val mock = mockk<Divider>()

        every { mock.divide(capture(slot), any()) } returns 22

        mock.divide(5, 2) shouldBeEqualTo 22
        slot.captured shouldBeEqualTo 5
    }

    @Test
    fun `capturing slot and return answers`() {
        val slot = slot<Int>()
        val mock = mockk<Divider>()

        every { mock.divide(capture(slot), any()) } answers { slot.captured * 11 }
        mock.divide(5, 2) shouldBeEqualTo 55

        clearMocks(mock)

        every { mock.divide(any(), capture(slot)) } answers { slot.captured * 11 }
        mock.divide(5, 2) shouldBeEqualTo 22
    }

    @Test
    fun `relaxed mock`() {

        val mock = mockk<Divider>(relaxed = true)

        mock.divide(5, 2) shouldBeEqualTo 0

        verify { mock.divide(5, 2) }
    }

    class Adder {
        fun magnify(a: Int) = a
        fun add(a: Int, b: Int) = a + magnify(b)
    }

    /**
     * Spy 는 기존 method를 교체하여 다른 형태로 정의할 수 있습니다.
     *
     * Spies give the possibility to set expected behavior and do behavior verification
     * while still executing original methods of an object.
     */
    @Test
    fun `spies object`() {
        val spy = spyk<Adder>()

        spy.add(4, 5) shouldBeEqualTo (4 + 5)

        // magnify 함수를 재정의한다
        every { spy.magnify(any()) } answers { firstArg<Int>() * 2 }

        spy.add(4, 5) shouldBeEqualTo (4 + 5 * 2)

        verify { spy.add(4, 5) }
        verify { spy.magnify(5) }
    }
}