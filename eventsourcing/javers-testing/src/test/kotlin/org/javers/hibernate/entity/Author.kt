package org.javers.hibernate.entity

import java.io.Serializable
import java.time.LocalDate
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "author")
@Access(AccessType.PROPERTY)  // NOTE: AccessType 이 Property인 경우에는 모든 annotation 앞에 get 을 붙여야 합니다.
data class Author(@get:Id var id: String = "",
                  var name: String? = null,
                  var birthday: LocalDate = LocalDate.now()) : Serializable
