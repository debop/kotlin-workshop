package io.github.debop.ahocorasick.trie

interface Token {
    val fragment: String
    fun isMatch(): Boolean
    val emit: Emit?
}

abstract class AbstractToken(override val fragment: String): Token

class MatchToken(fragment: String, override val emit: Emit): AbstractToken(fragment) {
    override fun isMatch(): Boolean = true
}

class FragmentToken(fragment: String): AbstractToken(fragment) {
    override fun isMatch(): Boolean = false
    override val emit: Emit? = null
}