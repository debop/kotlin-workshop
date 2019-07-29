package io.github.debop.ahocorasick.trie

interface EmitHandler {
    fun emit(emit: Emit): Boolean
}

interface StatefulEmitHandler: EmitHandler {
    val emits: MutableList<Emit>
}

abstract class AbstractStatefulEmitHandler: StatefulEmitHandler {

    override val emits: MutableList<Emit> = mutableListOf()

    fun addEmit(emit: Emit) {
        emits.add(emit)
    }
}

class DefaultEmitHandler: StatefulEmitHandler {

    override val emits: MutableList<Emit> = mutableListOf()

    override fun emit(emit: Emit): Boolean {
        return this.emits.add(emit)
    }
}