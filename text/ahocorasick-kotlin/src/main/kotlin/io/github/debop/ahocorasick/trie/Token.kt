package io.github.debop.ahocorasick.trie

interface Token {
    val fragment: String
    fun isMatch(): Boolean
    val emit: Emit?
}

abstract class AbstractToken(override val fragment: String): Token {
    override fun toString(): String = "Token(fragment=$fragment, emit=$emit)"
}

class MatchToken(fragment: String, override val emit: Emit): AbstractToken(fragment) {
    override fun isMatch(): Boolean = true
}

class FragmentToken(fragment: String): AbstractToken(fragment) {
    override fun isMatch(): Boolean = false
    override val emit: Emit? = null
}