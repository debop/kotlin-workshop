package io.github.debop.ahocorasick.trie

import mu.KLogging

/**
 * Trie
 * @author debop (Sunghyouk Bae)
 */
class Trie(private val config: TrieConfig) {

    companion object: KLogging() {
        fun builder(): TrieBuilder = TrieBuilder()
    }

    val rootState: State = State()

    private val isCaseInsensitive: Boolean get() = config.caseInsensitive

    suspend fun tokenize(text: String): MutableList<Token> {
        val tokens = mutableListOf<Token>()
        var lastCollectionIndex = -1

        val collectedEmits = parseText(text)

        TODO("구현 중")
    }

    suspend fun parseText(text: CharSequence, emitHandler: StatefulEmitHandler = DefaultEmitHandler()): List<Emit> {
        TODO("구현 중")
    }

    suspend fun containsMatch(text: CharSequence): Boolean = firstMatch(text) != null

    fun runParseText(text: CharSequence, emitHandler: EmitHandler) {
        TODO("구현 중")
    }

    /**
     * The first matching text sequence.
     *
     * @param text The text to search for keywords
     * @return null if no matches found.
     */
    suspend fun firstMatch(text: CharSequence): Emit? {
        TODO("구현 중")
    }

    private fun addKeyword(keyword: String) {
        if (keyword.isNotEmpty()) {
            if (isCaseInsensitive) {
                val lower = keyword.toLowerCase()
                addState(lower).addEmit(lower)
            } else {
                addState(keyword).addEmit(keyword)
            }
        }
    }

    private fun addKeywords(vararg keywords: String) {
        keywords.forEach { addKeyword(it) }
    }

    private fun addKeywords(keywords: Collection<String>) {
        keywords.forEach { addKeyword(it) }
    }

    private fun addState(keyword: String): State = rootState.addState(keyword)


    private fun createFragment(emit: Emit?, text: String, lastCollectedPosition: Int): Token {
        return FragmentToken(text.substring(lastCollectedPosition + 1, emit?.start ?: text.length))
    }

    private fun createMatch(emit: Emit, text: String): Token {
        return MatchToken(text.substring(emit.start, emit.end + 1), emit)
    }


    private fun isPartialMatch(searchText: CharSequence, emit: Emit): Boolean {
        TODO("구현 중")
    }

    private fun removePartialMatches(searchText: CharSequence, collectedEmits: MutableList<Emit>) {
        collectedEmits.removeIf { isPartialMatch(searchText, it) }
    }

    private fun removePartialMatchesWhiteSpaceSeparated(searchText: CharSequence, collectedEmits: MutableList<Emit>) {
        TODO("구현 중")
    }

    private fun getState(currentState: State, ch: Char): State {
        var thisState = currentState
        var nextState = thisState.nextState(ch)
        while (nextState == null) {
            thisState = thisState.failure!!
            nextState = thisState.nextState(ch)
        }
        return nextState
    }

    private fun constructFailureStates() {
        TODO("구현 중")
    }

    private fun storeEmits(position: Int, currentState: State, emitHandler: EmitHandler): Boolean {
        TODO("구현 중")
    }

    /**
     * Trie Builder class
     */
    class TrieBuilder {
        private val config = TrieConfig()
        private val trie = Trie(config)

        fun ignoreClass() = apply {
            config.caseInsensitive = true
        }

        fun ignoreOverlaps() = apply {
            config.allowOverlaps = false
        }

        fun addKeyword(keyword: String) = apply {
            trie.addKeyword(keyword)
        }

        fun addKeywords(vararg keywords: String) = apply {
            trie.addKeywords(*keywords)
        }

        fun addKeywords(keywords: Collection<String>) = apply {
            trie.addKeywords(keywords)
        }

        fun onlyWholeWords() = apply {
            config.onlyWholeWords = true
        }

        fun stopOnHit() = apply {
            config.stopOnHit = true
        }

        fun build(): Trie {
            trie.constructFailureStates()
            return trie
        }
    }
}