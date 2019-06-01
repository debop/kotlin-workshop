package io.github.debop.kotlin.workshop.units

import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
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

        a + a shouldEqual b
        b - a shouldEqual a
        a * 2 shouldEqual b
        2 * a shouldEqual b
        b / 2 shouldEqual a
    }

    @Test
    fun `compare weights`() {
        assertTrue { 1.7.gram() > 1.65.gram() }
        assertTrue { 1001.gram() > 1.0.kilogram() }

        assertTrue { -4.gram() > -5.gram() }
    }

    @Test
    fun `convert unit of weight`() {
        1.0.milligram().inMilligram() shouldEqualTo 1.0
        10.gram().inGram() shouldEqualTo 10.0

        1.0.gram().inMilligram() shouldEqualTo 1e3
        100.gram().inKilogram() shouldEqualTo 0.1

        100.kilogram().inTon() shouldEqual 0.1
        100.kilogram().inGram() shouldEqual 100 * 1e3
    }

    @Test
    fun `display human string of weight`() {
        100.gram().toHuman() shouldEqual "100.0 g"
        12.47.milligram().toHuman() shouldEqual "12.5 mg"
        1234.43.gram().toHuman() shouldEqual "1.2 kg"

        (-1.4e3).ton().toHuman() shouldEqual "-1400.0 ton"

        Weight.ZERO.toHuman() shouldEqual "0.0 mg"
        Weight.MIN_VALUE.toHuman() shouldEqual "0.0 mg"
        Weight.MAX_VALUE.toHuman() shouldEqual "%.1f ton".format(Double.MAX_VALUE / WeightUnit.TON.factor)

        Weight.POSITIVE_INF.toHuman() shouldEqual "Infinity ton"
        Weight.NEGATIVE_INF.toHuman() shouldEqual "-Infinity ton"

        Weight.NaN.toHuman() shouldEqual "NaN"
    }

    @Test
    fun `parse invalid weight string`() {
        null.toWeight() shouldEqual Weight.NaN
        "".toWeight() shouldEqual Weight.NaN
        "   ".toWeight() shouldEqual Weight.NaN
        " \t ".toWeight() shouldEqual Weight.NaN

        assertThrows<NumberFormatException> {
            "abc".toWeight()
        }
        assertThrows<NumberFormatException> {
            "-123 gg".toWeight()
        }
    }

    @Test
    fun `parse valid weight string`() {
        "1.4 mg".toWeight() shouldEqual 1.4.milligram()
        "123.45 g".toWeight() shouldEqual 123.45.gram()
        "54 mg".toWeight() shouldEqual 54.milligram()
        "0.4 kg".toWeight() shouldEqual 400.gram()

        "Infinity mg".toWeight() shouldEqual Weight.POSITIVE_INF
        "-Infinity ton".toWeight() shouldEqual Weight.NEGATIVE_INF

        "NaN".toWeight() shouldEqual Weight.NaN
    }
}