package org.javers.core.model

import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.ShallowReference

/**
 * ShallowPhone
 *
 * @author debop
 * @since 19. 7. 15
 */
@ShallowReference
data class ShallowPhone(@Id var id: Long,
                        var number: String? = null,
                        var category: CategoryC? = null)