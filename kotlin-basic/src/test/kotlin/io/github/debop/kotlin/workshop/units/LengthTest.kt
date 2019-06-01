package io.github.debop.kotlin.workshop.units

import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

/**
 * LengthTest
 * @author debop (Sunghyouk Bae)
 */
class LengthTest {

    companion object : KLogging()

    @Test
    fun `operation for length`() {
        val a = 100.meter()
        val b = 200.meter()

        a + a shouldEqual b
        b - a shouldEqual a
        a * 2 shouldEqual b
        2 * a shouldEqual b
        b / 2 shouldEqual a
    }

    @Test
    fun `compare lengths`() {
        assertTrue { 1.7.meter() > 1.65.meter() }
        assertTrue { 101.centimeter() > 1.meter() }
        assertTrue { -4.meter() > -500.centimeter() }
    }

    @Test
    fun `conversion of length units`() {
        1.0.millimeter().inMillimeter() shouldEqualTo 1.0
        100.0.meter().inMeter() shouldEqualTo 100.0

        100.centimeter().inMeter() shouldEqualTo 1.0
        1.6.kilometer().inMeter() shouldEqualTo 1600.0

        100.kilometer().inMeter() shouldEqualTo 100 * 1e3
        100.millimeter().inMeter() shouldEqualTo 100 * 1e-3
        100.centimeter().inMeter() shouldEqualTo 100 * 1e-2
    }

    @Test
    fun `display human string`() {
        100.meter().toHuman() shouldEqual "100.0 m"
        12.47.millimeter().toHuman() shouldEqual "1.2 cm"
        123.43.centimeter().toHuman() shouldEqual "1.2 m"
        0.45.kilometer().toHuman() shouldEqual "450.0 m"
        14493.meter().toHuman() shouldEqual "14.5 km"

        (-1.3e6).kilometer().toHuman() shouldEqual "-1300000.0 km"

        Length.ZERO.toHuman() shouldEqual "0.0 mm"
        Length.MIN_VALUE.toHuman() shouldEqual "0.0 mm"
        Length.MAX_VALUE.toHuman() shouldEqual "%.1f km".format(Double.MAX_VALUE / LengthUnit.KILOMETER.factor)
        Length.POSITIVE_INF.toHuman() shouldEqual "Infinity km"
        Length.NEGATIVE_INF.toHuman() shouldEqual "-Infinity km"

        Length.NaN.toHuman() shouldEqual "NaN"
    }

    @Test
    fun `parse invalid length string`() {
        null.toLength() shouldEqual Length.NaN
        "".toLength() shouldEqual Length.NaN
        "   ".toLength() shouldEqual Length.NaN
        " \t ".toLength() shouldEqual Length.NaN

        assertThrows<NumberFormatException> {
            "abc".toLength() shouldEqual Length.NaN
        }

        assertThrows<NumberFormatException> {
            "-123 kk".toLength() shouldEqual Length.NaN
        }
    }

    @Test
    fun `parse valid length string`() {
        "1.2 cm".toLength() shouldEqual 1.2.centimeter()
        "123.45 m".toLength() shouldEqual 123.45.meter()
        "0.54 mm".toLength() shouldEqual 0.54.millimeter()
        "12.4 km".toLength() shouldEqual 12.4.kilometer()

        "Infinity mm".toLength() shouldEqual Length.POSITIVE_INF
        "-Infinity mm".toLength() shouldEqual Length.NEGATIVE_INF

        "NaN".toLength() shouldEqual Length.NaN
    }
}