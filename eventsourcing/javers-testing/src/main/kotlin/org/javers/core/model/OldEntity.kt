package org.javers.core.model

import org.javers.core.metamodel.annotation.Id

/**
 * OldEntity
 *
 * @author debop
 * @since 19. 7. 17
 */
data class OldEntity(@Id val id: Int,
                     val value: Int = 0,
                     val oldValue: Int = 0)