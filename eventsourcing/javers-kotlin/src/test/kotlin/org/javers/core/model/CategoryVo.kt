package org.javers.core.model

/**
 * CategoryVo
 *
 * @author debop
 * @since 19. 7. 15
 */
class CategoryVo(var name: String? = null) {

    var parent: CategoryVo? = null
    val children: MutableList<CategoryVo> = mutableListOf()

    fun addChild(child: CategoryVo) {
        child.parent = this
        children.add(child)
    }
}