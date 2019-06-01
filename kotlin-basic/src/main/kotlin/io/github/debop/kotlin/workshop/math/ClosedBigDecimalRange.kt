package io.github.debop.kotlin.workshop.math

import java.io.Serializable
import java.math.BigDecimal
import java.util.Objects

@SinceKotlin("1.1")
operator fun BigDecimal.rangeTo(upperBound: Number): ClosedBigDecimalRange =
    ClosedBigDecimalRange(this, upperBound)

val ClosedBigDecimalRange.length: BigDecimal
    get() = (endInclusive - start + BigDecimal.ONE)

fun ClosedBigDecimalRange.contains(other: ClosedBigDecimalRange): Boolean =
    start <= other.start && endInclusive >= other.endInclusive

fun Iterable<ClosedBigDecimalRange>.isAscending(): Boolean =
    fold(true to BigDecimal.ZERO) { acc, e ->
        (acc.first && acc.second <= e.start) to e.start
    }.first

interface ClosedBigNumberRange<T : Comparable<T>> : ClosedRange<T>, Serializable {

    override operator fun contains(value: T): Boolean =
        lessThanOrEqual(start, value) && lessThanOrEqual(value, endInclusive)

    override fun isEmpty(): Boolean =
        !lessThanOrEqual(start, endInclusive)

    fun lessThanOrEqual(a: T, b: T): Boolean
}

/**
 * Closed range by BigDecimal
 *
 * @author debop (Sunghyouk Bae)
 */
@Suppress("ConvertTwoComparisonsToRangeCheck")
class ClosedBigDecimalRange(lowerBound: Number,
                            upperBound: Number) : ClosedBigNumberRange<BigDecimal> {

    override val start: BigDecimal = lowerBound.toBigDecimal()
    override val endInclusive: BigDecimal = upperBound.toBigDecimal()

    override fun lessThanOrEqual(a: BigDecimal, b: BigDecimal) = a <= b

    override operator fun contains(value: BigDecimal): Boolean = start <= value && value <= endInclusive

    operator fun contains(value: Number): Boolean = start <= value && endInclusive >= value

    override fun isEmpty(): Boolean = start > endInclusive

    override fun equals(other: Any?): Boolean =
        (other is ClosedBigDecimalRange)
        && (isEmpty() && other.isEmpty() || (start == other.start && endInclusive == other.endInclusive))

    override fun hashCode(): Int = Objects.hash(start, endInclusive)

    override fun toString(): String = "$start..$endInclusive"

}