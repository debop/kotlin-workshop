package org.javers.core.model

import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import java.math.BigDecimal

/**
 * NewEntityWithTypeAlias
 *
 * @author debop
 * @since 19. 7. 17
 */
@TypeName("myName")
@Entity
class NewEntityWithTypeAlias(@Id var id: BigDecimal) {

    var value: Int = 0

    var valueObject: NewValueObjectWithTypeAlias? = null
}