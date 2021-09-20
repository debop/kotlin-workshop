package io.github.debop.kotlin.workshop.units

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

/**
 * WeightTest
 * @author debop (Sunghyouk Bae)
 */
class WeightTest {

    companion object : KLogging()

    @Test
    fun `operation for weight`() {
        val a = 100.gram()
        val b = 200.gram()

        a + a shouldBeEqualTo b
        b - a shouldBeEqualTo a
        a * 2 shouldBeEqualTo b
        2 * a shouldBeEqualTo b
        b / 2 shouldBeEqualTo a
    }

    @Test
    fun `compare weights`() {
        assertTrue { 1.7.gram() > 1.65.gram() }
        assertTrue { 1001.gram() > 1.0.kilogram() }

        assertTrue { -4.gram() > -5.gram() }
    }

    @Test
    fun `convert unit of weight`() {
        1.0.milligram().inMilligram() shouldBeEqualTo 1.0
        10.gram().inGram() shouldBeEqualTo 10.0

        1.0.gram().inMilligram() shouldBeEqualTo 1e3
        100.gram().inKilogram() shouldBeEqualTo 0.1

        100.kilogram().inTon() shouldBeEqualTo 0.1
        100.kilogram().inGram() shouldBeEqualTo 100 * 1e3
    }

    @Test
    fun `display human string of weight`() {
        100.gram().toHuman() shouldBeEqualTo "100.0 g"
        12.47.milligram().toHuman() shouldBeEqualTo "12.5 mg"
        1234.43.gram().toHuman() shouldBeEqualTo "1.2 kg"

        (-1.4e3).ton().toHuman() shouldBeEqualTo "-1400.0 ton"

        Weight.ZERO.toHuman() shouldBeEqualTo "0.0 mg"
        Weight.MIN_VALUE.toHuman() shouldBeEqualTo "0.0 mg"
        Weight.MAX_VALUE.toHuman() shouldBeEqualTo "%.1f ton".format(Double.MAX_VALUE / WeightUnit.TON.factor)

        Weight.POSITIVE_INF.toHuman() shouldBeEqualTo "Infinity ton"
        Weight.NEGATIVE_INF.toHuman() shouldBeEqualTo "-Infinity ton"

        Weight.NaN.toHuman() shouldBeEqualTo "NaN"
    }

    @Test
    fun `parse invalid weight string`() {
        null.toWeight() shouldBeEqualTo Weight.NaN
        "".toWeight() shouldBeEqualTo Weight.NaN
        "   ".toWeight() shouldBeEqualTo Weight.NaN
        " \t ".toWeight() shouldBeEqualTo Weight.NaN

        assertThrows<NumberFormatException> {
            "abc".toWeight()
        }
        assertThrows<NumberFormatException> {
            "-123 gg".toWeight()
        }
    }

    @Test
    fun `parse valid weight string`() {
        "1.4 mg".toWeight() shouldBeEqualTo 1.4.milligram()
        "123.45 g".toWeight() shouldBeEqualTo 123.45.gram()
        "54 mg".toWeight() shouldBeEqualTo 54.milligram()
        "0.4 kg".toWeight() shouldBeEqualTo 400.gram()

        "Infinity mg".toWeight() shouldBeEqualTo Weight.POSITIVE_INF
        "-Infinity ton".toWeight() shouldBeEqualTo Weight.NEGATIVE_INF

        "NaN".toWeight() shouldBeEqualTo Weight.NaN
    }
}