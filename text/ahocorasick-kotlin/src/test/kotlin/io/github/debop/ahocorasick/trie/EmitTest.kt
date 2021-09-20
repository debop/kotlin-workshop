package io.github.debop.ahocorasick.trie

import io.github.debop.kotlin.tests.extensions.Randomized
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotEqual
import org.junit.jupiter.api.Test

@Randomized
class EmitTest {

    @Test
    fun `emit equals`() {
        val e1 = Emit(13, 42, null)
        val e2 = Emit(13, 42, null)
        val e3 = Emit(13, 42, "keyword")

        e1 shouldBeEqualTo e2
        e1 shouldBeEqualTo e3
    }

    @Test
    fun `emit not equals`() {
        val e1 = Emit(13, 42, null)
        val e2 = Emit(0, 1, null)

        e1 shouldNotEqual e2
    }
}