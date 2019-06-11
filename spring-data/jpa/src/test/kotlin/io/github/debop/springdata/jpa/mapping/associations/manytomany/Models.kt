package io.github.debop.springdata.jpa.mapping.associations.manytomany

import java.io.Serializable
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany

@Entity
data class BankAccount(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,
    val number: String
) : Serializable {

    // NOTE: many-to-many 관계에서는 cascade에 REMOVE, DETACH를 포함시키면 상대 entity도 삭제되므로 조심해야 한다.
    @ManyToMany(mappedBy = "accounts", cascade = [PERSIST, MERGE, REFRESH], fetch = EAGER)
    val owners: MutableSet<AccountOwner> = hashSetOf()

    fun addOwners(vararg owners: AccountOwner) {
        owners.forEach {
            if (this.owners.add(it)) {
                it.accounts.add(this)
            }
        }
    }

    fun removeOwners(vararg owners: AccountOwner) {
        owners.forEach {
            if (this.owners.remove(it)) {
                it.accounts.remove(this)
            }
        }
    }
}

@Entity
data class AccountOwner(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,
    val ssn: String
) : Serializable {

    // NOTE: many-to-many 관계에서는 cascade에 REMOVE, DETACH를 포함시키면 상대 entity도 삭제되므로 조심해야 한다.
    @ManyToMany(cascade = [PERSIST, MERGE, REFRESH], fetch = LAZY)
    @JoinTable(name = "account_owner_bank_account_map",
               joinColumns = [JoinColumn(name = "owner_id")],
               inverseJoinColumns = [JoinColumn(name = "account_id")])
    var accounts: MutableSet<BankAccount> = hashSetOf()

    fun addAccounts(vararg accounts: BankAccount) {
        accounts.forEach {
            if (this.accounts.add(it)) {
                it.owners.add(this)
            }
        }
    }

    fun removeAccounts(vararg accounts: BankAccount) {
        accounts.forEach {
            if (this.accounts.remove(it)) {
                it.owners.remove(this)
            }
        }
    }
}

