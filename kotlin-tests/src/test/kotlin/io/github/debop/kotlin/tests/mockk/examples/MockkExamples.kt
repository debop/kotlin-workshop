package io.github.debop.kotlin.tests.mockk.examples

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

interface Dependency1 {
    val value1: Int
    fun call(arg: Int): Int
}

interface Dependency2 {
    val value2: String
    fun call(arg: Int): Int
}

class SystemUnderTest(val dependency1: Dependency1, val dependency2: Dependency2) {
    fun calculate() = dependency1.value1 + dependency2.value2.toInt()
}

class MockkExamples {

    @Test
    fun `mock dependency objects`() {
        val doc1 = mockk<Dependency1>()
        val doc2 = mockk<Dependency2>()

        every { doc1.value1 } returns 5
        every { doc2.value2 } returns "6"

        val sut = SystemUnderTest(doc1, doc2)
        sut.calculate() shouldEqualTo 11

        verify(exactly = 1) {
            doc1.value1
            doc2.value2
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

        mock.divide(5, 2) shouldEqual 22
        slot.captured shouldEqual 5
    }

    @Test
    fun `capturing slot and return answers`() {
        val slot = slot<Int>()
        val mock = mockk<Divider>()

        every { mock.divide(capture(slot), any()) } answers { slot.captured * 11 }
        mock.divide(5, 2) shouldEqualTo 55

        clearMocks(mock)

        every { mock.divide(any(), capture(slot)) } answers { slot.captured * 11 }
        mock.divide(5, 2) shouldEqualTo 22
    }

    @Test
    fun `relaxed mock`() {

        val mock = mockk<Divider>(relaxed = true)

        mock.divide(5, 2) shouldEqualTo 0

        verify { mock.divide(5, 2) }
    }

    class Adder {
        fun magnify(a: Int) = a
        fun add(a: Int, b: Int) = a + magnify(b)
    }

    @Test
    fun `spies object`() {
        val spy = spyk<Adder>()

        spy.add(4, 5) shouldEqual (4 + 5)

        // magnify 함수를 재정의한다
        every { spy.magnify(any()) } answers { firstArg<Int>() * 2 }
        spy.add(4, 5) shouldEqual (4 + 5 * 2)

        verify { spy.add(4, 5) }
        verify { spy.magnify(5) }
    }
}