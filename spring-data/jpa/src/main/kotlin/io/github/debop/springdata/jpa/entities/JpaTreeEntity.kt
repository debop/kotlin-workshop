package io.github.debop.springdata.jpa.entities

/**
 * JpaTreeEntity
 * @author debop (Sunghyouk Bae)
 */
interface JpaTreeEntity<T : JpaTreeEntity<T>> {

    var parent: T?

    val children: MutableSet<T>

    @Suppress("UNCHECKED_CAST")
    fun addChildren(vararg childsToAdd: T) {
        childsToAdd.forEach {
            if (children.add(it)) {
                it.parent = this as T
            }
        }
    }

    fun removeChildren(vararg childsToRemove: T) {
        childsToRemove.forEach {
            if (children.remove(it)) {
                it.parent = null
            }
        }
    }
}
