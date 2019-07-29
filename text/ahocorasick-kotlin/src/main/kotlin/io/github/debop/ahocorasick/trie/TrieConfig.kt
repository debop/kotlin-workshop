package io.github.debop.ahocorasick.trie

import java.io.Serializable

/**
 * TrieConfig
 * @author debop (Sunghyouk Bae)
 */
data class TrieConfig(
    val allowOverlaps: Boolean = true,
    val onlyWholeWords: Boolean = false,
    val onlyWholeWordsWhiteSpaceSeparated: Boolean = false,
    val caseInsensitive: Boolean = false,
    val stopOnHit: Boolean = false
): Serializable