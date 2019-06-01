package io.github.debop.kotlin.workshop.concurrent

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater

/**
 * RoundRobinAtomicInt
 * @author debop (Sunghyouk Bae)
 */
class RoundRobinAtomicInt(val maximum: Int) {

    init {
        require(maximum > 0) { "Maximum must be greater than 0. maximum=$maximum" }
    }

    @Volatile private var currentValue: Int = 0

    private val updater =
        AtomicIntegerFieldUpdater.newUpdater(RoundRobinAtomicInt::class.java, "currentValue")

    /**
     * Get the current value
     */
    fun get(): Int = currentValue

    /**
     * Get the next value, incrementing the value or reset to zero for the next caller
     */
    fun next(): Int {
        if (maximum <= 1) {
            return 0
        }
        while (true) {
            val current = get()
            val next = if (current < maximum - 1) current + 1 else 0
            if (updater.compareAndSet(this, current, next)) {
                return next
            }
        }
    }
}