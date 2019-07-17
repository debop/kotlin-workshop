package org.javers.core.model

import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName

/**
 * NewEntity
 *
 * @author debop
 * @since 19. 7. 17
 */
@TypeName("org.javers.core.examples.typeNames.OldEntity")
data class NewEntity(@Id val id: Int,
                     val value: Int = 0,
                     val newValue: Int = 0)