package io.github.debop.kotlin.workshop.units

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
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

        a + a shouldBeEqualTo b
        b - a shouldBeEqualTo a
        a * 2 shouldBeEqualTo b
        2 * a shouldBeEqualTo b
        b / 2 shouldBeEqualTo a
    }

    @Test
    fun `compare lengths`() {
        assertTrue { 1.7.meter() > 1.65.meter() }
        assertTrue { 101.centimeter() > 1.meter() }
        assertTrue { -4.meter() > -500.centimeter() }
    }

    @Test
    fun `conversion of length units`() {
        1.0.millimeter().inMillimeter() shouldBeEqualTo 1.0
        100.0.meter().inMeter() shouldBeEqualTo 100.0

        100.centimeter().inMeter() shouldBeEqualTo 1.0
        1.6.kilometer().inMeter() shouldBeEqualTo 1600.0

        100.kilometer().inMeter() shouldBeEqualTo 100 * 1e3
        100.millimeter().inMeter() shouldBeEqualTo 100 * 1e-3
        100.centimeter().inMeter() shouldBeEqualTo 100 * 1e-2
    }

    @Test
    fun `display human string`() {
        100.meter().toHuman() shouldBeEqualTo "100.0 m"
        12.47.millimeter().toHuman() shouldBeEqualTo "1.2 cm"
        123.43.centimeter().toHuman() shouldBeEqualTo "1.2 m"
        0.45.kilometer().toHuman() shouldBeEqualTo "450.0 m"
        14493.meter().toHuman() shouldBeEqualTo "14.5 km"

        (-1.3e6).kilometer().toHuman() shouldBeEqualTo "-1300000.0 km"

        Length.ZERO.toHuman() shouldBeEqualTo "0.0 mm"
        Length.MIN_VALUE.toHuman() shouldBeEqualTo "0.0 mm"
        Length.MAX_VALUE.toHuman() shouldBeEqualTo "%.1f km".format(Double.MAX_VALUE / LengthUnit.KILOMETER.factor)
        Length.POSITIVE_INF.toHuman() shouldBeEqualTo "Infinity km"
        Length.NEGATIVE_INF.toHuman() shouldBeEqualTo "-Infinity km"

        Length.NaN.toHuman() shouldBeEqualTo "NaN"
    }

    @Test
    fun `parse invalid length string`() {
        null.toLength() shouldBeEqualTo Length.NaN
        "".toLength() shouldBeEqualTo Length.NaN
        "   ".toLength() shouldBeEqualTo Length.NaN
        " \t ".toLength() shouldBeEqualTo Length.NaN

        assertThrows<NumberFormatException> {
            "abc".toLength() shouldBeEqualTo Length.NaN
        }

        assertThrows<NumberFormatException> {
            "-123 kk".toLength() shouldBeEqualTo Length.NaN
        }
    }

    @Test
    fun `parse valid length string`() {
        "1.2 cm".toLength() shouldBeEqualTo 1.2.centimeter()
        "123.45 m".toLength() shouldBeEqualTo 123.45.meter()
        "0.54 mm".toLength() shouldBeEqualTo 0.54.millimeter()
        "12.4 km".toLength() shouldBeEqualTo 12.4.kilometer()

        "Infinity mm".toLength() shouldBeEqualTo Length.POSITIVE_INF
        "-Infinity mm".toLength() shouldBeEqualTo Length.NEGATIVE_INF

        "NaN".toLength() shouldBeEqualTo Length.NaN
    }
}