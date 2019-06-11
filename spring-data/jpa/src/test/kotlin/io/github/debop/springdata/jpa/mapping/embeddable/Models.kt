package io.github.debop.springdata.jpa.mapping.embeddable

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.io.Serializable
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table


@Embeddable
data class EmbeddableAddress(var street: String? = null,
                             var city: String? = null,
                             var zipcode: String? = null): Serializable

@Entity(name = "embeddable_employee")
@Table(indexes = [
    Index(name = "ix_embeddable_employee_name", columnList = "username, password"),
    Index(name = "ix_embeddable_employee_email", columnList = "email")
])
@DynamicInsert
@DynamicUpdate
data class Employee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(length = 128, nullable = false)
    val username: String = "",
    @Column(length = 64, nullable = false)
    val password: String = ""): Serializable {

    var email: String? = null
    var active: Boolean = true


    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "street", column = Column(name = "home_street", length = 128)),
        AttributeOverride(name = "city", column = Column(name = "home_city", length = 24)),
        AttributeOverride(name = "zipcode", column = Column(name = "home_zipcode", length = 8))
    )
    val homeAddress = EmbeddableAddress()

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "street", column = Column(name = "office_street", length = 128)),
        AttributeOverride(name = "city", column = Column(name = "office_city", length = 24)),
        AttributeOverride(name = "zipcode", column = Column(name = "office_zipcode", length = 8))
    )
    val officeAddress = EmbeddableAddress()
}