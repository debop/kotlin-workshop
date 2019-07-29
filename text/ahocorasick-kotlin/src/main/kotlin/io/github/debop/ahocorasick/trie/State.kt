package io.github.debop.ahocorasick.trie

import mu.KLogging
import java.io.Serializable

/**
 * State
 * @author debop (Sunghyouk Bae)
 */
class State(val depth: Int = 0): Serializable {

    companion object: KLogging()

}