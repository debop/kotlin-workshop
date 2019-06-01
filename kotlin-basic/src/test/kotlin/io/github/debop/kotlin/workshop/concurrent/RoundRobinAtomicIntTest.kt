package io.github.debop.kotlin.workshop.concurrent

import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * RoundRobinAtomicIntTest
 * @author debop (Sunghyouk Bae)
 */
class RoundRobinAtomicIntTest {

    @Test
    fun `invalid maximum`() {
        assertThrows<IllegalArgumentException> {
            RoundRobinAtomicInt(0)
        }
    }

    @Test
    fun `round limits with maximum is 1`() {
        val atomic = RoundRobinAtomicInt(1)

        atomic.get() shouldEqualTo 0

        atomic.next() shouldEqualTo 0
        atomic.next() shouldEqualTo 0
        atomic.next() shouldEqualTo 0
    }

    @Test
    fun `round limits`() {
        val atomic = RoundRobinAtomicInt(4)

        atomic.get() shouldEqualTo 0

        atomic.next() shouldEqualTo 1
        atomic.next() shouldEqualTo 2
        atomic.next() shouldEqualTo 3
        atomic.next() shouldEqualTo 0
        atomic.next() shouldEqualTo 1
        atomic.next() shouldEqualTo 2
        atomic.next() shouldEqualTo 3
        atomic.next() shouldEqualTo 0
    }
}