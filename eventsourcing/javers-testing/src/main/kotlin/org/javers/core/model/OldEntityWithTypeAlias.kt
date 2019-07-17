package org.javers.core.model

import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName

/**
 * OldEntityWithTypeAlias
 *
 * @author debop
 * @since 19. 7. 17
 */
@TypeName("myName")
@Entity
data class OldEntityWithTypeAlias(@get:Id val id: Int,
                                  val value: Int = 0,
                                  val oldValue: Int = 0)