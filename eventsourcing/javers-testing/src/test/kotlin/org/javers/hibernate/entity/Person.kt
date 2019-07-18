package org.javers.hibernate.entity

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "person")
@Access(AccessType.FIELD)
data class Person(@Id var id: String, var name: String? = null) {

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = true)
    var boss: Person? = null

    fun getBoss(level: Int): Person? {
        return when {
            level <= 0 -> this
            level == 1 -> boss
            else       -> boss?.getBoss(level - 1)
        }
    }
}