package io.github.debop.ahocorasick.interval

/**
 * Kotlin [IntRange]와 같은 기능을 수행합니다. 범위는 [start, end] 입니다.
 * @author debop (Sunghyouk Bae)
 */
open class Interval(override val start: Int,
                    override val end: Int): Intervalable {

    companion object {
        val EMPTY = Interval(1, 0)
    }

    fun overlapsWith(other: Interval): Boolean =
        start < other.end && end >= other.start

    fun overlapsWith(point: Int): Boolean =
        start <= point && point <= end

    fun isEmpty(): Boolean = start > end

    override fun compareTo(other: Intervalable): Int {
        var comparison = start - other.start
        if (comparison == 0) {
            comparison = end - other.end
        }
        return comparison
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            is Intervalable -> start == other.start && end == other.end
            else            -> false
        }

    override fun hashCode(): Int {
        return if (isEmpty()) -1
        else start + 31 * end
    }

    override fun toString(): String {
        return "$start:$end"
    }
}