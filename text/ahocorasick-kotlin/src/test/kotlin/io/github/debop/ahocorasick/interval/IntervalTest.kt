package io.github.debop.ahocorasick.interval

import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import java.util.TreeSet

/**
 * IntervalTest
 * @author debop (Sunghyouk Bae)
 */
class IntervalTest {

    @Test
    fun `proper range properties`() {
        val interval = Interval(1, 3)
        interval.start shouldEqualTo 1
        interval.end shouldEqualTo 3
    }

    @Test
    fun `proper size property`() {
        Interval(0, 2).size shouldEqualTo 3
        Interval(5, 8).size shouldEqualTo 4
    }

    @Test
    fun `intervals overlaps`() {
        val i1 = Interval(1, 3)
        val i2 = Interval(2, 4)
        val i3 = Interval(9, 12)

        i1.overlapsWith(i2).shouldBeTrue()
        i2.overlapsWith(i1).shouldBeTrue()

        i1.overlapsWith(i3).shouldBeFalse()
        i3.overlapsWith(i1).shouldBeFalse()

        i2.overlapsWith(i3).shouldBeFalse()
        i3.overlapsWith(i2).shouldBeFalse()
    }

    @Test
    fun `interval overlaps with point`() {

        val i1 = Interval(1, 3)

        i1.overlapsWith(i1.start).shouldBeTrue()
        i1.overlapsWith(i1.end).shouldBeTrue()

        i1.overlapsWith(i1.start - 1).shouldBeFalse()
        i1.overlapsWith(i1.end + 1).shouldBeFalse()
    }

    @Test
    fun `compare intervals`() {
        Interval(0, 1).compareTo(Interval(0, 2)) shouldBeLessThan 0
        Interval(0, 1).compareTo(Interval(1, 2)) shouldBeLessThan 0

        Interval(0, 2).compareTo(Interval(0, 1)) shouldBeGreaterThan 0
        Interval(1, 2).compareTo(Interval(0, 2)) shouldBeGreaterThan 0
    }

    @Test
    fun `interval sort with comparable`() {
        val intervals = TreeSet<Interval>()

        intervals.add(Interval(4, 6))
        intervals.add(Interval(2, 7))
        intervals.add(Interval(3, 4))

        val iter = intervals.iterator()
        iter.next().start shouldEqualTo 2
        iter.next().start shouldEqualTo 3
        iter.next().start shouldEqualTo 4
    }
}