package org.javers.hibernate.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "author")
data class Author(@Id var id: String? = null,
                  var name: String? = null)