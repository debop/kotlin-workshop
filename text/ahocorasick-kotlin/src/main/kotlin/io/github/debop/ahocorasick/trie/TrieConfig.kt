package io.github.debop.ahocorasick.trie

import java.io.Serializable

/**
 * TrieConfig
 * @author debop (Sunghyouk Bae)
 */
data class TrieConfig(
    var allowOverlaps: Boolean = true,
    var onlyWholeWords: Boolean = false,
    var onlyWholeWordsWhiteSpaceSeparated: Boolean = false,
    var caseInsensitive: Boolean = false,
    var stopOnHit: Boolean = false
): Serializable