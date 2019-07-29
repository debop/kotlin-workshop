package io.github.debop.ahocorasick.interval

import java.io.Serializable

/**
 * Intervalable
 * @author debop (Sunghyouk Bae)
 */
interface Intervalable: Comparable<Intervalable>, Serializable {

    val start: Int
    val end: Int
    val size: Int get() = end - start + 1

}