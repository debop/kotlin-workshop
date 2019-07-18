package org.javers.spring.boot.model

import java.io.Serializable
import java.util.UUID
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "dummy_entity")
@Access(AccessType.PROPERTY)
data class DummyEntity(@get:Id var id: Int,
                       var name: String? = null): Serializable {

    companion object {
        fun random(): DummyEntity = DummyEntity(UUID.randomUUID().hashCode(), UUID.randomUUID().toString())
    }

    @get:ManyToOne
    var shallowEntity: ShallowEntity? = null
}