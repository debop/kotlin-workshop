package io.github.debop.kotlin.workshop.units

import io.github.debop.kotlin.workshop.units.LengthUnit.CENTIMETER
import io.github.debop.kotlin.workshop.units.LengthUnit.KILOMETER
import io.github.debop.kotlin.workshop.units.LengthUnit.METER
import io.github.debop.kotlin.workshop.units.LengthUnit.MILLIMETER
import mu.KLogging
import java.io.Serializable
import kotlin.math.absoluteValue

fun lengthOf(length: Number = 0.0, unit: LengthUnit = MILLIMETER): Length = Length(length.toDouble() * unit.factor)

fun <T : Number> T.length(unit: LengthUnit): Length = lengthOf(this, unit)

fun <T : Number> T.millimeter(): Length = length(MILLIMETER)
fun <T : Number> T.centimeter(): Length = length(CENTIMETER)
fun <T : Number> T.meter(): Length = length(METER)
fun <T : Number> T.kilometer(): Length = length(KILOMETER)

operator fun <T : Number> T.times(length: Length): Length = length * this.toDouble()

fun String?.toLength(): Length = Length.parse(this)

/**
 * LengthUnit
 * @author debop (Sunghyouk Bae)
 */
enum class LengthUnit(val unitName: String, val factor: Double) {

    MILLIMETER("mm", 1.0),
    CENTIMETER("cm", 10.0),
    METER("m", 1000.0),
    KILOMETER("km", 1e6);

    companion object {
        @JvmStatic
        fun parse(unitName: String): LengthUnit {
            val lower = unitName.toLowerCase()
            return values().find { it.unitName == lower }
                   ?: throw UnsupportedOperationException("Unknown Length unit name. unitName=$unitName")
        }
    }
}

/**
 * 길이를 나타내는 클래스
 * 사칙연산을 정의하여 표현할 수 있고, 단위 변환을 함수로 나타낼 수 있다
 *
 * @author debop (Sunghyouk Bae)
 */
data class Length(val value: Double) : Comparable<Length>, Serializable {

    operator fun plus(other: Length): Length = Length(value = this.value + other.value)
    operator fun minus(other: Length): Length = Length(value = this.value - other.value)
    operator fun times(scalar: Number): Length = Length(value * scalar.toDouble())
    operator fun div(scalar: Number): Length = Length(value / scalar.toDouble())
    operator fun unaryMinus(): Length = Length(value.unaryMinus())

    fun inMillimeter(): Double = value / MILLIMETER.factor
    fun inCentimeter(): Double = value / LengthUnit.CENTIMETER.factor
    fun inMeter(): Double = value / LengthUnit.METER.factor
    fun inKilometer(): Double = value / LengthUnit.KILOMETER.factor

    override fun compareTo(other: Length): Int = value.compareTo(other.value)
    override fun toString(): String = toHuman()

    fun toHuman(): String {
        if (value.isNaN()) {
            return Double.NaN.toString()
        }
        if (RESERVED_VALUES.contains(this)) {
            toHuman(KILOMETER)
        }
        val absValue = value.absoluteValue
        val displayUnit = LengthUnit.values().lastOrNull { absValue / it.factor > 1.0 } ?: MILLIMETER

        return toHuman(displayUnit)
    }

    fun toHuman(unit: LengthUnit): String {
        if (value.isNaN()) {
            return Double.NaN.toString()
        }
        return "%.1f %s".format(value / unit.factor, unit.unitName)
    }

    companion object : KLogging() {

        @JvmField val ZERO = Length(0.0)
        @JvmField val MIN_VALUE = Length(Double.MIN_VALUE)
        @JvmField val MAX_VALUE = Length(Double.MAX_VALUE)
        @JvmField val POSITIVE_INF = Length(Double.POSITIVE_INFINITY)
        @JvmField val NEGATIVE_INF = Length(Double.NEGATIVE_INFINITY)
        @JvmField val NaN = Length(Double.NaN)

        @JvmField
        val RESERVED_VALUES = listOf(MIN_VALUE, MAX_VALUE, POSITIVE_INF, NEGATIVE_INF, NaN)

        @JvmStatic
        fun parse(expr: String?): Length {
            if (expr.isNullOrBlank()) {
                return NaN
            }
            if (expr == "NaN") {
                return NaN
            }
            try {
                val (length, unit) = expr.trim().split(" ", limit = 2)
                return Length(length.toDouble() * LengthUnit.parse(unit).factor)
            } catch (e: Exception) {
                throw NumberFormatException("Invalid Length string. expr=$expr")
            }
        }
    }
}