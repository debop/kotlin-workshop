package io.github.debop.kotlin.workshop.operators

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * TimeOperatorsTest
 * @author debop (Sunghyouk Bae)
 */
class TimeOperatorsTest {

    companion object : KLogging()

    @ParameterizedTest
    @ValueSource(bytes = [Byte.MIN_VALUE, -1])
    fun `times operator with negative byte`(count: Byte) {
        assertThrows<IllegalArgumentException> {
            var runs: Byte = 0
            count times { runs++ }
        }
    }

    @ParameterizedTest
    @ValueSource(bytes = [0, 1, Byte.MAX_VALUE])
    fun `times operator with byte`(count: Byte) {
        var runs: Byte = 0
        count times { runs++ }
        runs shouldBeEqualTo count
    }

    @ParameterizedTest
    @ValueSource(ints = [Int.MIN_VALUE, -1])
    fun `times operator with negative int`(count: Int) {
        assertThrows<IllegalArgumentException> {
            var runs = 0
            count times { runs++ }
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 1024])
    fun `times operator with int`(count: Int) {
        var runs = 0
        count times { runs++ }
        runs shouldBeEqualTo count
    }

    @ParameterizedTest
    @ValueSource(longs = [Long.MIN_VALUE, -1])
    fun `times operator with negative long`(count: Long) {
        assertThrows<IllegalArgumentException> {
            var runs = 0
            count times { runs++ }
        }
    }

    @ParameterizedTest
    @ValueSource(longs = [0, 1, 1024L])
    fun `times operator with int`(count: Long) {
        var runs = 0L
        count times { runs++ }
        runs shouldBeEqualTo count
    }
}