package io.github.debop.ahocorasick.interval

import java.io.Serializable

/**
 * IntervalNode
 * @author debop (Sunghyouk Bae)
 */
open class IntervalNode(aIntervals: List<Intervalable>): Serializable {

    enum class Direction { LEFT, RIGHT }

    var left: IntervalNode? = null
    var right: IntervalNode? = null
    val point = determineMedian(aIntervals)

    val intervals: MutableList<Intervalable> = mutableListOf()

    init {

    }

    private fun determineMedian(intervals: List<Intervalable>): Int {
        TODO("구현 중")
    }

    fun findOverlaps(interval: Intervalable): MutableList<Intervalable> {
        TODO("구현 중")
    }

    protected fun addToOverlaps(interval: Intervalable,
                                overlaps: MutableList<Intervalable>,
                                newOverlaps: List<Intervalable>) {
        overlaps.addAll(newOverlaps.filter { it != interval })
    }

    protected fun checkForOverlapsToTheLeft(interval: Intervalable) =
        checkForOverlaps(interval, Direction.LEFT)

    protected fun checkForOverlapsToTheRight(interval: Intervalable) =
        checkForOverlaps(interval, Direction.RIGHT)

    protected fun checkForOverlaps(interval: Intervalable, direction: Direction): MutableList<Intervalable> {
        TODO("구현 중")
    }

    protected fun findOverlappingRanges(node: IntervalNode?, interval: Intervalable): MutableList<Intervalable> {
        return node?.findOverlaps(interval) ?: mutableListOf()
    }
}