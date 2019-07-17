package org.javers.hibernate.entity

import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "ebook")
data class Ebook(@Id var id: String? = null) {

    var title: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    var author: Author? = null

    @ElementCollection
    val comments: MutableList<String> = mutableListOf()
}