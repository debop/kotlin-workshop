package io.github.debop.kotlin.workshop.operators


inline infix operator fun Byte.times(action: (Byte) -> Unit) {
    require(this >= 0) { "times number must not be negative value. number=$this" }
    var current: Byte = 0
    while(current < this) {
        action.invoke(current++)
    }
}

inline infix operator fun Int.times(action: (Int) -> Unit) {
    require(this >= 0) { "times number must not be negative value. number=$this" }
    repeat(this) { action.invoke(it) }
}

inline infix operator fun Long.times(action: (Long) -> Unit) {
    require(this >= 0) { "times number must not be negative value. number=$this" }
    var current = 0L
    while(current < this) {
        action.invoke(current++)
    }
}
