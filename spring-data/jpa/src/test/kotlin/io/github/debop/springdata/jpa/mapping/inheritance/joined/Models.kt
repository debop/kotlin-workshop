package io.github.debop.springdata.jpa.mapping.inheritance.joined

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.io.Serializable
import javax.persistence.CascadeType.ALL
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table


@Entity(name = "joined_person")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(indexes = [Index(name = "ix_joined_person_name", columnList = "personName, ssn")])
abstract class AbstractJoinedPerson(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "personName", nullable = false, length = 128)
    open val name: String = "",

    @Column(name = "ssn", nullable = false, length = 24)
    open val ssn: String = ""
): Serializable

@Entity(name = "joined_employee")
@Table(indexes = [Index(name = "ix_joined_employee_empno", columnList = "empNo")])
@DynamicInsert
@DynamicUpdate
data class Employee(
    override val name: String = "",
    override val ssn: String = "",
    @Column(name = "empNo", nullable = false, length = 12)
    val empNo: String = ""
): AbstractJoinedPerson() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    var manager: Employee? = null

    @OneToMany(mappedBy = "manager", cascade = [ALL])
    val members: MutableSet<Employee> = hashSetOf()
}


@Entity(name = "joined_customer")
@Table(indexes = [Index(name = "ix_joined_customer_mobile", columnList = "mobile")])
@DynamicInsert
@DynamicUpdate
data class Customer(
    override val name: String = "",
    override val ssn: String = "",
    @Column(name = "mobile", nullable = false, length = 16)
    val mobile: String = ""
): AbstractJoinedPerson() {

    @ManyToOne
    @JoinColumn(name = "contact_employee_id")
    var contactEmployee: Employee? = null

}