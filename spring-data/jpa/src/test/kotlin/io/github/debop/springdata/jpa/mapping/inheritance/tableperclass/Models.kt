package io.github.debop.springdata.jpa.mapping.inheritance.tableperclass

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.GenericGenerator
import java.io.Serializable
import java.util.Date
import java.util.UUID
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Inheritance
import javax.persistence.InheritanceType
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Access(AccessType.FIELD)
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name = "tableperclass_billing",
       indexes = [Index(name = "ix_tableperclass_billing_owner", columnList = "owner")])
abstract class AbstractBilling(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    var id: UUID? = null,
    open val owner: String = ""
): Serializable


@Entity(name = "tableperclass_creditcard")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
data class CreditCard(override val owner: String, val number: String? = null): AbstractBilling() {

    var companyName: String? = null
    var expMonth: Int? = null
    var expYear: Int? = null

    @Temporal(TemporalType.DATE)
    var startDate: Date? = null

    @Temporal(TemporalType.DATE)
    var endDate: Date? = null

    var swift: String? = null
}

@Entity(name = "tableperclass_bankaccount")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
data class BankAccount(override val owner: String, val account: String? = null): AbstractBilling() {

    var bankname: String? = null
    var swift: String? = null
}