package org.javers.spring.boot.model

import org.javers.core.metamodel.annotation.ShallowReference
import java.io.Serializable
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@Access(AccessType.PROPERTY)
@ShallowReference
data class ShallowEntity(@get:Id var id: Int,
                         var value: String? = null): Serializable