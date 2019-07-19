package org.javers.spring.boot.model

import org.javers.core.metamodel.annotation.ShallowReference
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@ShallowReference
data class ShallowEntity(
    @Id
    var id: Int,
    var value: String? = null
): Serializable