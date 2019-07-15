package org.javers.core.model

import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.ShallowReference

/**
 * PhoneWithShallowCategory
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 15
 */
data class PhoneWithShallowCategory(@Id var id: Long) {

    var number: String = "123"
    var deepCategory: CategoryC? = null

    @ShallowReference
    var shallowCategory: CategoryC? = null

    @ShallowReference
    var shallowCategories: MutableSet<CategoryC> = mutableSetOf()

    @ShallowReference
    var shallowCategoryList: MutableList<CategoryC> = mutableListOf()

    @ShallowReference
    var shallowCategoryMap: MutableMap<String, CategoryC> = mutableMapOf()

}