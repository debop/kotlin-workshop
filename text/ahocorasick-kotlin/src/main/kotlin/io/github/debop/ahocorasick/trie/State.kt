package io.github.debop.ahocorasick.trie

import mu.KLogging
import org.eclipse.collections.impl.map.mutable.UnifiedMap
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet
import java.io.Serializable

/**
 * State
 * @author debop (Sunghyouk Bae)
 */
class State(val depth: Int = 0): Serializable {

    companion object: KLogging()

    private val rootState: State? get() = if (depth == 0) this else null
    private val success = UnifiedMap<Char, State>()
    var failure: State? = null
    private val emits = TreeSortedSet<String>()

    fun nextState(ch: Char, ignoreRootState: Boolean = false): State? {
        var nextState = this.success[ch]

        val canUseRootState = !ignoreRootState && nextState == null && rootState != null
        if (canUseRootState) {
            nextState = rootState
        }
        return nextState
    }

    fun nextStateIgnoreRootState(ch: Char): State? = nextState(ch, true)

    fun addState(keyword: String): State {
        var state = this
        keyword.forEach { state = state.addState(it) }
        return state
    }

    fun addState(ch: Char): State {
        var nextState = nextStateIgnoreRootState(ch)
        if (nextState == null) {
            nextState = State(this.depth + 1)
            success[ch] = nextState
        }
        return nextState
    }

    fun addEmit(keyword: String) {
        this.emits.add(keyword)
    }

    fun addEmit(vararg emits: String) {
        this.emits.addAll(emits)
    }

    fun emit(): Collection<String> = this.emits

    fun getStates(): Collection<State> = this.success.values

    fun getTransitions(): Collection<Char> = this.success.keys

}