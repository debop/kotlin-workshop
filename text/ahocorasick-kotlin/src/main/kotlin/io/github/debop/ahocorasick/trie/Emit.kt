package io.github.debop.ahocorasick.trie

import io.github.debop.ahocorasick.interval.Interval

/**
 * Emit
 * @author debop (Sunghyouk Bae)
 */
class Emit(override val start: Int,
           override val end: Int,
           val keyword: String? = null): Interval(start, end) {

    override fun toString(): String {
        return super.toString() + "=${keyword ?: "<null>"}"
    }
}