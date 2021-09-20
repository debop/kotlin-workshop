package io.github.debop.ahocorasick.interval

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

/**
 * IntervalComparatorTest
 * @author debop (Sunghyouk Bae)
 */
class IntervalableComparatorTest {

    companion object: KLogging() {
        val sizeComparator = IntervalableComparatorBySize()
        val sizeReverseComparator = IntervalableComparatorBySizeReverse()
        val positionComparator = IntervalableComparatorByPosition()
    }

    @Test
    fun `compare intervalable by position`() {
        val intervals = mutableListOf(
            Interval(4, 5),
            Interval(1, 4),
            Interval(3, 8)
        )

        intervals.sortWith(positionComparator)

        intervals[0] shouldBeEqualTo Interval(1, 4)
        intervals[1] shouldBeEqualTo Interval(3, 8)
        intervals[2] shouldBeEqualTo Interval(4, 5)
    }

    @Test
    fun `compare intervalable by size`() {
        val intervals = mutableListOf(
            Interval(4, 5),
            Interval(1, 4),
            Interval(3, 8)
        )

        intervals.sortWith(sizeComparator)

        intervals[0].size shouldBeEqualTo 2
        intervals[1].size shouldBeEqualTo 4
        intervals[2].size shouldBeEqualTo 6
    }

    @Test
    fun `compare intervalable by size reverse`() {
        val intervals = mutableListOf(
            Interval(4, 5),
            Interval(1, 4),
            Interval(3, 8)
        )

        intervals.sortWith(sizeReverseComparator)

        intervals[0].size shouldBeEqualTo 6
        intervals[1].size shouldBeEqualTo 4
        intervals[2].size shouldBeEqualTo 2
    }

    @Test
    fun `compare intervalable by size reverse and position`() {
        val intervals = mutableListOf(
            Interval(4, 7),
            Interval(2, 5),
            Interval(3, 6)
        )

        intervals.sortWith(sizeReverseComparator)

        intervals[0] shouldBeEqualTo Interval(2, 5)
        intervals[1] shouldBeEqualTo Interval(3, 6)
        intervals[2] shouldBeEqualTo Interval(4, 7)
    }
}