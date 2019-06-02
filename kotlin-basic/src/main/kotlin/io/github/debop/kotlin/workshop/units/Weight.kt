package io.github.debop.kotlin.workshop.units

import io.github.debop.kotlin.workshop.units.WeightUnit.GRAM
import io.github.debop.kotlin.workshop.units.WeightUnit.KILOGRAM
import io.github.debop.kotlin.workshop.units.WeightUnit.MILLIGRAM
import io.github.debop.kotlin.workshop.units.WeightUnit.TON
import mu.KLogging
import java.io.Serializable
import kotlin.math.absoluteValue

fun weightOf(weight: Number = 0.0, weightUnit: WeightUnit = MILLIGRAM) =
    Weight(weight.toDouble() * weightUnit.factor)

fun <T : Number> T.weight(unit: WeightUnit): Weight = weightOf(this, unit)

fun <T : Number> T.milligram(): Weight = weight(MILLIGRAM)
fun <T : Number> T.gram(): Weight = weight(GRAM)
fun <T : Number> T.kilogram(): Weight = weight(KILOGRAM)
fun <T : Number> T.ton(): Weight = weight(TON)

operator fun <T : Number> T.times(weight: Weight): Weight = weight * this.toDouble()

fun String?.toWeight(): Weight = Weight.parse(this)

enum class WeightUnit(val unitName: String, val factor: Double) {
    MILLIGRAM("mg", 1.0),
    GRAM("g", 1e3),
    KILOGRAM("kg", 1e6),
    TON("ton", 1e9);

    companion object {

        @JvmStatic
        fun parse(unitName: String): WeightUnit {
            val lower = unitName.toLowerCase()
            return values().find { it.unitName == lower }
                   ?: throw UnsupportedOperationException("Unknown Weight unit name. unitName=$unitName")
        }
    }
}

/**
 * 무게를 나타내는 클래스
 * 사칙연산을 정의하여 표현할 수 있고, 단위 변환을 함수로 나타낼 수 있다
 *
 * @author debop (Sunghyouk Bae)
 */
data class Weight(val value: Double) : Comparable<Weight>, Serializable {

    operator fun plus(other: Weight) = Weight(value + other.value)
    operator fun minus(other: Weight) = Weight(value - other.value)
    operator fun times(scalar: Number) = Weight(value * scalar.toDouble())
    operator fun div(scalar: Number) = Weight(value / scalar.toDouble())
    operator fun unaryMinus() = Weight(value.unaryMinus())

    fun inMilligram(): Double = value / MILLIGRAM.factor
    fun inGram(): Double = value / GRAM.factor
    fun inKilogram(): Double = value / KILOGRAM.factor
    fun inTon(): Double = value / TON.factor

    override fun compareTo(other: Weight): Int = value.compareTo(other.value)

    override fun toString(): String = toHuman()

    fun toHuman(): String {
        if (value.isNaN()) {
            return Double.NaN.toString()
        }
        if (RESERVED_VALUES.contains(this)) {
            toHuman(TON)
        }
        val absValue = value.absoluteValue
        val displayUnit = WeightUnit.values().lastOrNull { absValue / it.factor > 1.0 } ?: MILLIGRAM

        return toHuman(displayUnit)
    }

    fun toHuman(unit: WeightUnit): String {
        if (value.isNaN()) {
            return Double.NaN.toString()
        }
        return "%.1f %s".format(value / unit.factor, unit.unitName)
    }

    companion object : KLogging() {

        @JvmField val ZERO = Weight(0.0)
        @JvmField val MIN_VALUE = Weight(Double.MIN_VALUE)
        @JvmField val MAX_VALUE = Weight(Double.MAX_VALUE)
        @JvmField val POSITIVE_INF = Weight(Double.POSITIVE_INFINITY)
        @JvmField val NEGATIVE_INF = Weight(Double.NEGATIVE_INFINITY)
        @JvmField val NaN = Weight(Double.NaN)

        @JvmField
        val RESERVED_VALUES = listOf(MIN_VALUE, MAX_VALUE, POSITIVE_INF, NEGATIVE_INF, NaN)

        @JvmStatic
        fun parse(expr: String?): Weight {
            if (expr.isNullOrBlank()) {
                return NaN
            }
            if (expr == "NaN") {
                return NaN
            }
            try {
                val (weight, unit) = expr.trim().split(" ", limit = 2)
                return Weight(weight.toDouble() * WeightUnit.parse(unit).factor)
            } catch (e: Exception) {
                throw NumberFormatException("Invalid Weight string. expr=$expr")
            }
        }
    }
}