package io.github.debop.kotlin.workshop.math

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * ClosedBigDecimalRangeTest
 * @author debop (Sunghyouk Bae)
 */
class ClosedBigDecimalRangeTest {

    companion object : KLogging()

    @Test
    fun `create closed range of big decimal `() {
        val range1 = ClosedBigDecimalRange(0, 1)
        val range2 = BigDecimal.ZERO..BigDecimal.ONE

        range2 shouldBeEqualTo range1
    }

    @Test
    fun `check range length`() {
        (BigDecimal.ZERO..BigDecimal.ONE).length shouldBeEqualTo 2.toBigDecimal()

        val range = 0.5.toBigDecimal()..4.5.toBigDecimal()
        range.length shouldBeEqualTo 5.0.toBigDecimal()

        (-5.toBigDecimal()..5.toBigDecimal()).length shouldBeEqualTo 11.toBigDecimal()
    }

    @Test
    fun `contains of bigdecimal ranges`() {
        val range = BigDecimal.ONE..BigDecimal.TEN

        range.contains(BigDecimal.ONE).shouldBeTrue()
        range.contains(BigDecimal.TEN).shouldBeTrue()
        range.contains(5.toBigDecimal()).shouldBeTrue()
        range.contains(15.toBigDecimal()).shouldBeFalse()

        (5 in range) shouldBeEqualTo true
        (15 in range) shouldBeEqualTo false
    }

    @Test
    fun `comparing ranges`() {
        val range1 = 10.toBigDecimal()..100.toBigDecimal()
        val range2 = 1.toBigDecimal()..101.toBigDecimal()
        val range3 = 11.toBigDecimal()..21.toBigDecimal()

        listOf(range1, range2, range3).isAscending() shouldBeEqualTo false
        listOf(range2, range1, range3).isAscending() shouldBeEqualTo true
    }
}