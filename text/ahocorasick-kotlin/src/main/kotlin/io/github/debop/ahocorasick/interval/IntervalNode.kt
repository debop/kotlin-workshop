package io.github.debop.ahocorasick.interval

import java.io.Serializable
import java.util.LinkedList

/**
 * IntervalNode
 * @author debop (Sunghyouk Bae)
 */
open class IntervalNode(inputs: Iterable<Intervalable>): Serializable {

    enum class Direction { LEFT, RIGHT }

    var left: IntervalNode? = null
    var right: IntervalNode? = null
    val point: Int

    val intervals = LinkedList<Intervalable>()

    init {
        point = determineMedian(inputs)
        buildTree(inputs)
    }

    private fun determineMedian(inputs: Iterable<Intervalable>): Int {
        val start = inputs.map { it.start }.minOrNull() ?: 0
        val end = inputs.map { it.end }.maxOrNull() ?: 0
        return (start + end) / 2
    }

    private fun buildTree(inputs: Iterable<Intervalable>) {
        val toLeft = LinkedList<Intervalable>()
        val toRight = LinkedList<Intervalable>()

        inputs.forEach {
            when {
                it.end < point   -> toLeft.add(it)
                it.start > point -> toRight.add(it)
                else             -> intervals.add(it)
            }
        }
        if (toLeft.isNotEmpty()) {
            this.left = IntervalNode(toLeft)
        }
        if (toRight.isNotEmpty()) {
            this.right = IntervalNode(toRight)
        }
    }

    suspend fun findOverlaps(interval: Intervalable): MutableList<Intervalable> {
        val overlaps = mutableListOf<Intervalable>()

        when {
            interval.start > point -> {
                addToOverlaps(interval, overlaps, findOverlappingRanges(right, interval))
                addToOverlaps(interval, overlaps, checkForOverlapsToTheRight(interval))
            }
            interval.end < point   -> {
                addToOverlaps(interval, overlaps, findOverlappingRanges(left, interval))
                addToOverlaps(interval, overlaps, checkForOverlapsToTheLeft(interval))
            }
            else                   -> {
                addToOverlaps(interval, overlaps, this.intervals)
                addToOverlaps(interval, overlaps, findOverlappingRanges(left, interval))
                addToOverlaps(interval, overlaps, findOverlappingRanges(right, interval))
            }
        }
        return overlaps
    }

    protected fun addToOverlaps(interval: Intervalable,
                                overlaps: MutableList<Intervalable>,
                                newOverlaps: List<Intervalable>) {
        newOverlaps
            .filter { it != interval }
            .forEach { overlaps.add(it) }
    }

    protected suspend fun checkForOverlapsToTheLeft(interval: Intervalable) =
        checkForOverlaps(interval, Direction.LEFT)

    protected suspend fun checkForOverlapsToTheRight(interval: Intervalable) =
        checkForOverlaps(interval, Direction.RIGHT)

    protected fun checkForOverlaps(interval: Intervalable, direction: Direction): List<Intervalable> {
        val overlaps = mutableListOf<Intervalable>()

        this.intervals.forEach {
            when (direction) {
                Direction.LEFT  ->
                    if (it.start <= interval.end) {
                        overlaps.add(it)
                    }
                Direction.RIGHT ->
                    if (it.end >= interval.start) {
                        overlaps.add(it)
                    }
            }
        }
        return overlaps
    }

    protected suspend fun findOverlappingRanges(node: IntervalNode?, interval: Intervalable): List<Intervalable> {
        return node?.findOverlaps(interval) ?: mutableListOf()
    }
}