package io.github.debop.ahocorasick.trie

import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

/**
 * StateTest
 * @author debop (Sunghyouk Bae)
 */
class StateTest {

    @Test
    fun `ctor sequence of characters`() {
        val rootState = State()

        rootState
            .addState('a')
            .addState('b')
            .addState('c')

        val nextState1 = rootState.nextState('a')

        nextState1.shouldNotBeNull()
        nextState1.depth shouldEqualTo 1

        val nextState2 = nextState1.nextState('b')
        nextState2.shouldNotBeNull()
        nextState2.depth shouldEqualTo 2

        val nextState3 = nextState2.nextState('c')
        nextState3.shouldNotBeNull()
        nextState3.depth shouldEqualTo 3
    }

    @Test
    fun `addEmit to State`() {
        val rootState = State()

        rootState.addEmit("abc")
        rootState.addEmit("def")
        rootState.addEmit("ghi")

        rootState.emit() shouldContainSame listOf("abc", "def", "ghi")
    }
}