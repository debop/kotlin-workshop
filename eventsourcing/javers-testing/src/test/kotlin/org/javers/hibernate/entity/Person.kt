package org.javers.hibernate.entity

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "person")
data class Person(@Id var id: String? = null, var name: String? = null) {

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var boss: Person? = null

    fun getBoss(level: Int): Person? {
        return when {
            level <= 0 -> this
            level == 1 -> boss
            else       -> boss?.getBoss(level - 1)
        }
    }
}