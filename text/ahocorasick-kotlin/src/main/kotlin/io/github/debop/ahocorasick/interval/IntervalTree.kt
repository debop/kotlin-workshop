package io.github.debop.ahocorasick.interval

import mu.KLogging
import java.io.Serializable

/**
 * IntervalTree
 * @author debop (Sunghyouk Bae)
 */
class IntervalTree(private val rootNode: IntervalNode): Serializable {

    constructor(intervals: List<Intervalable>): this(IntervalNode(intervals))

    companion object: KLogging() {
        val sizeReverseComparator = IntervalableComparatorBySizeReverse()
        val positionComparator = IntervalableComparatorByPosition()
    }

    suspend fun <T: Intervalable> removeOverlaps(intervals: Iterable<T>): MutableList<T> {
        // size가 큰 것부터
        val results = intervals.toMutableList()
        results.sortWith(sizeReverseComparator)

        val removed = mutableSetOf<Intervalable>()

        results.forEach {
            if (!removed.contains(it)) {
                val overlaps = findOverlaps(it)
                logger.trace { "target=$it, overlaps=$overlaps" }
                removed.addAll(overlaps)
            }
        }
        // remove all intervals that was overlapping
        logger.debug { "removed=$removed" }
        results.removeAll(removed)

        // sort the intervals, now on left-most position only
        results.sortWith(positionComparator)
        return results
    }

    suspend fun findOverlaps(interval: Intervalable): MutableList<Intervalable> =
        rootNode.findOverlaps(interval)
}