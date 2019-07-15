package org.javers.core.model

/**
 * AbstractCategory
 *
 * @author debop
 * @since 19. 7. 15
 */
abstract class AbstractCategory @JvmOverloads constructor(var name: String? = null) {

    var parent: AbstractCategory? = null
    val categories: MutableList<AbstractCategory> = mutableListOf()

    fun addChild(child: AbstractCategory) {
        child.parent = this
        this.categories.add(child)
    }
}