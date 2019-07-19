package org.javers.spring.boot.model

import java.io.Serializable
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class DummyEntity(
    @Id
    var id: Int = 0,
    var name: String? = null
): Serializable {

    companion object {
        fun random(): DummyEntity = DummyEntity(UUID.randomUUID().hashCode(), UUID.randomUUID().toString())
    }

    @ManyToOne
    var shallowEntity: ShallowEntity? = null
}