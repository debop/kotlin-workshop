package org.javers.hibernate.entity

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.CascadeType
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "ebook")
@Access(AccessType.PROPERTY)  // NOTE: AccessType 이 Property인 경우에는 모든 annotation 앞에 get 을 붙여야 합니다.
data class Ebook(@get:Id var id: String) {

    var title: String? = null

    @get:ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    var author: Author? = null

    @get:ElementCollection(targetClass = String::class, fetch = FetchType.EAGER)
    var comments: MutableList<String>? = mutableListOf()
}