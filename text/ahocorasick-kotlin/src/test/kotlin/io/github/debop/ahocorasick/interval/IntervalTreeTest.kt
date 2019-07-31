package io.github.debop.ahocorasick.interval

import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

/**
 * IntervalTreeTest
 * @author debop (Sunghyouk Bae)
 */
class IntervalTreeTest {

    companion object: KLogging()

    @Test
    fun `find overlaps`() = runBlocking<Unit> {
        val intervals = List(6) { Interval(it, it + 2) }
        val tree = IntervalTree(intervals)

        val overlaps = tree.findOverlaps(Interval(1, 3))
        overlaps.size shouldEqual 3

        overlaps[0] shouldEqual Interval(2, 4)
        overlaps[1] shouldEqual Interval(3, 5)
        overlaps[2] shouldEqual Interval(0, 2)
    }

    @Test
    fun `find overlaps with various size`() = runBlocking<Unit> {
        val intervals = listOf(
            Interval(0, 2),
            Interval(4, 5),
            Interval(2, 10),
            Interval(6, 13),
            Interval(9, 15),
            Interval(12, 16)
        )
        val tree = IntervalTree(intervals)

        tree.findOverlaps(Interval(0, 2)) shouldContainSame listOf(Interval(2, 10))
        tree.findOverlaps(Interval(4, 5)) shouldContainSame listOf(Interval(2, 10))
        tree.findOverlaps(Interval(2, 10)) shouldContainSame listOf(Interval(0, 2), Interval(4, 5), Interval(6, 13), Interval(9, 15))

        tree.findOverlaps(Interval(6, 13)) shouldContainSame listOf(Interval(2, 10), Interval(9, 15), Interval(12, 16))
        tree.findOverlaps(Interval(9, 15)) shouldContainSame listOf(Interval(2, 10), Interval(6, 13), Interval(12, 16))
        tree.findOverlaps(Interval(12, 16)) shouldContainSame listOf(Interval(6, 13), Interval(9, 15))
    }

    @Test
    fun `remove overlap`() = runBlocking<Unit> {
        val intervals = listOf(
            Interval(0, 2),
            Interval(4, 5),
            Interval(2, 10),
            Interval(6, 13),
            Interval(9, 15),
            Interval(12, 16)
        )
        val tree = IntervalTree(intervals)

        val removed = tree.removeOverlaps(intervals.toMutableList())
        logger.debug { "removed overlaps=$removed" }
        removed.size shouldEqualTo 2
        removed shouldContainSame listOf(Interval(2, 10), Interval(12, 16))
    }

    private fun assertOverlaps(interval: Intervalable, expectedStart: Int, expectedEnd: Int) {
        interval.start shouldEqualTo expectedStart
        interval.end shouldEqualTo expectedEnd
    }
}