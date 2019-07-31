package io.github.debop.ahocorasick.trie

import io.github.debop.ahocorasick.interval.IntervalTree
import mu.KLogging
import java.util.ArrayDeque
import java.util.LinkedList

/**
 * Based on the Aho-Corasick white paper, Bell technologies:
 * http://cr.yp.to/bib/1975/aho.pdf
 *
 * @author debop (Sunghyouk Bae)
 */
class Trie(private val config: TrieConfig) {

    companion object: KLogging() {
        fun builder(): TrieBuilder = TrieBuilder()
    }

    val rootState: State = State()

    private val isCaseInsensitive: Boolean get() = config.caseInsensitive

    suspend fun replace(text: String, map: Map<String, String>): String {
        val tokens = tokenize(text)

        return buildString {
            tokens.forEach { token ->
                val keyword = token.emit?.keyword
                if (keyword != null && map.containsKey(keyword)) {
                    append(map[keyword])
                } else {
                    append(token.fragment)
                }
            }
        }
    }

    suspend fun tokenize(text: String): MutableList<Token> {
        val tokens = LinkedList<Token>()
        var lastCollectionIndex = -1

        val collectedEmits = parseText(text)

        collectedEmits.forEach { emit ->
            if (emit.start - lastCollectionIndex > 1) {
                tokens.add(createFragment(emit, text, lastCollectionIndex))
            }
            tokens.add(createMatch(emit, text))
            lastCollectionIndex = emit.end
        }

        if (text.length - lastCollectionIndex > 1) {
            tokens.add(createFragment(null, text, lastCollectionIndex))
        }
        return tokens
    }

    suspend fun parseText(text: CharSequence, emitHandler: StatefulEmitHandler = DefaultEmitHandler()): List<Emit> {
        runParseText(text, emitHandler)

        var collectedEmits = emitHandler.emits

        if (config.onlyWholeWords) {
            removePartialMatches(text, collectedEmits)
        }

        if (config.onlyWholeWordsWhiteSpaceSeparated) {
            removePartialMatchesWhiteSpaceSeparated(text, collectedEmits)
        }

        if (!config.allowOverlaps) {
            val intervalTree = IntervalTree(collectedEmits)
            collectedEmits = intervalTree.removeOverlaps(collectedEmits)
        }
        return collectedEmits
    }

    suspend fun containsMatch(text: CharSequence): Boolean = firstMatch(text) != null

    fun runParseText(text: CharSequence, emitHandler: EmitHandler) {
        var currentState = rootState

        text.forEachIndexed { pos, ch ->
            currentState = when {
                config.caseInsensitive -> getState(currentState, ch.toLowerCase())
                else                   -> getState(currentState, ch)
            }
            val stored = storeEmits(pos, currentState, emitHandler)
            if (stored && config.stopOnHit) {
                return
            }
        }
    }

    /**
     * The first matching text sequence.
     *
     * @param text The text to search for keywords
     * @return null if no matches found.
     */
    suspend fun firstMatch(text: CharSequence): Emit? {
        if (!config.allowOverlaps) {
            return parseText(text).firstOrNull()
        }

        var currentState = rootState

        text.forEachIndexed { pos, ch ->
            currentState = when {
                config.caseInsensitive -> getState(currentState, ch.toLowerCase())
                else                   -> getState(currentState, ch)
            }

            currentState.emit().forEach { emitStr ->
                val emit = Emit(pos - emitStr.length + 1, pos, emitStr)
                if (config.onlyWholeWords) {
                    if (!isPartialMatch(text, emit)) {
                        return emit
                    }
                } else {
                    return emit
                }
            }
        }
        logger.trace { "Not found matches. text=$text" }
        return null
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

    private fun addState(keyword: String): State =
        rootState.addState(keyword)


    private fun createFragment(emit: Emit?, text: String, lastCollectedPosition: Int): Token {
        return FragmentToken(text.substring(lastCollectedPosition + 1, emit?.start ?: text.length))
    }

    private fun createMatch(emit: Emit, text: String): Token {
        return MatchToken(text.substring(emit.start, emit.end + 1), emit)
    }

    private fun isPartialMatch(searchText: CharSequence, emit: Emit): Boolean {
        logger.trace { "Is partial match? searchText=$searchText, emit=$emit" }
        fun isAlphabeticStart() =
            emit.start != 0 && Character.isAlphabetic(searchText[emit.start - 1].toInt())

        if (isAlphabeticStart()) {
            return true
        }

        fun isAlphabeticEnd() =
            emit.end + 1 != searchText.length &&
            Character.isAlphabetic(searchText[emit.end + 1].toInt())

        return isAlphabeticEnd()
    }

    private fun removePartialMatches(searchText: CharSequence, collectedEmits: MutableList<Emit>) {
        collectedEmits.removeIf { isPartialMatch(searchText, it) }
    }

    private fun removePartialMatchesWhiteSpaceSeparated(searchText: CharSequence, collectedEmits: MutableList<Emit>) {
        val size = searchText.length

        collectedEmits.removeIf { emit ->
            val isEmptyStart = emit.start == 0 || Character.isWhitespace(searchText[emit.start - 1])
            if (!isEmptyStart) {
                true
            } else {
                val isEmptyEnd = emit.end + 1 == size || Character.isWhitespace(searchText[emit.end + 1])
                !isEmptyEnd
            }
        }
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
        val queue = ArrayDeque<State>()
        val startState = rootState

        // First, set the fail state of all depth 1 states to the root state
        startState.getStates().forEach { depthOneState ->
            depthOneState.failure = startState
            queue.add(depthOneState)
        }

        // Second, determine the fail state for all depth > 1 state
        while (!queue.isEmpty()) {
            val currentState = queue.remove()
            logger.trace { "currentState=$currentState" }

            currentState.getTransitions().forEach { transition ->
                val targetState = currentState.nextState(transition)
                check(targetState != null) { "targetState must not be null. transition=$transition, currentState=$currentState" }
                queue.add(targetState)

                var traceFailureState = currentState.failure!!
                while (traceFailureState.nextState(transition) == null) {
                    traceFailureState = traceFailureState.failure!!
                }
                val newFailureState = traceFailureState.nextState(transition)!!
                targetState.failure = newFailureState
                targetState.addEmits(newFailureState.emit())
            }
        }
    }

    private fun storeEmits(position: Int, currentState: State, emitHandler: EmitHandler): Boolean {
        var emitted = false
        val emits = currentState.emit()

        emits.forEach {
            emitted = emitHandler.emit(Emit(position - it.length + 1, position, it))
            if (emitted && config.stopOnHit) {
                return emitted
            }
        }
        return emitted
    }

    /**
     * Trie Builder class
     */
    class TrieBuilder {
        private val config = TrieConfig()
        private val trie = Trie(config)

        fun ignoreCase() = apply {
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

        fun onlyWholeWordsWhitespaceSeparated() = apply {
            config.onlyWholeWordsWhiteSpaceSeparated = true
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