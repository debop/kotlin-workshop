package io.github.debop.springdata.jpa.mapping.inheritance.singletable

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.io.Serializable
import java.util.Date
import javax.persistence.DiscriminatorValue
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType


@Entity(name = "singletable_billing")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "singletable_billing",
       indexes = [Index(name = "ix_singletable_billing_owner", columnList = "owner")])
abstract class AbstractBilling(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    open var owner: String = ""
): Serializable


@Entity(name = "singletable_creditcard")
@DiscriminatorValue("CARD")
@DynamicInsert
@DynamicUpdate
data class CreditCard(override var owner: String, var number: String? = null): AbstractBilling() {

    var companyName: String? = null
    var expMonth: Int? = null
    var expYear: Int? = null

    @Temporal(TemporalType.DATE)
    var startDate: Date? = null

    @Temporal(TemporalType.DATE)
    var endDate: Date? = null

    var swift: String? = null
}

@Entity(name = "singletable_bankaccount")
@DiscriminatorValue("ACCOUNT")
@DynamicInsert
@DynamicUpdate
data class BankAccount(override var owner: String, var account: String? = null): AbstractBilling() {

    var bankname: String? = null
    var swift: String? = null
}