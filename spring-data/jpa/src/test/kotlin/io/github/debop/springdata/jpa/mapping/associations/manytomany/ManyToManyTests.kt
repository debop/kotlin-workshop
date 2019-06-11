package io.github.debop.springdata.jpa.mapping.associations.manytomany

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

/**
 * ManyToManyTests
 *
 * @author debop
 * @since 19. 6. 5
 */
class ManyToManyTests : AbstractDataJpaTest() {

    @Autowired
    private lateinit var ownerRepo: AccountOwnerRepository
    @Autowired
    private lateinit var accountRepo: BankAccountRepository

    @Test
    fun `many-to-many manupulation by owner`() {

        val owner1 = AccountOwner(ssn = "681014-xxxxxxx")
        val owner2 = AccountOwner(ssn = "960522-xxxxxxx")

        val account1 = BankAccount(number = "111-01-xxxxxxx-01")
        val account2 = BankAccount(number = "222-01-xxxxxxx-02")
        val account3 = BankAccount(number = "333-01-xxxxxxx-03")
        val account4 = BankAccount(number = "444-01-xxxxxxx-04")

        owner1.addAccounts(account1, account2)
        owner2.addAccounts(account1, account3, account4)

        ownerRepo.save(owner1)
        ownerRepo.save(owner2)
        flushAndClear()

        var loaded1 = ownerRepo.findByIdOrNull(owner1.id)!!
        loaded1.accounts.size shouldEqualTo owner1.accounts.size

        var loaded2 = ownerRepo.findByIdOrNull(owner2.id)!!
        loaded2.accounts.size shouldEqualTo owner2.accounts.size

        val accountToRemove = accountRepo.findByIdOrNull(loaded2.accounts.first().id)!!
        loaded2.removeAccounts(accountToRemove)
        ownerRepo.save(loaded2)
        flushAndClear()

        loaded1 = ownerRepo.findByIdOrNull(owner1.id)!!
        loaded1.accounts.size shouldEqualTo owner1.accounts.size

        loaded2 = ownerRepo.findByIdOrNull(owner2.id)!!
        loaded2.accounts.size shouldEqualTo owner2.accounts.size - 1

        //
        // cascade 에 REMOVE 가 빠져 있다면, many-to-many 관계만 삭제된다.

        ownerRepo.delete(loaded2)
        flushAndClear()

        // owner2 와 mapping 된 account 가 삭제된 게 아니라, 관계만 끊어진 것임
        val removedAccount = accountRepo.findByIdOrNull(accountToRemove.id)
        removedAccount.shouldNotBeNull()
    }

    @Test
    fun `many-to-many manipulation by account`() {
        val owner1 = AccountOwner(ssn = "681014-xxxxxxx")
        val owner2 = AccountOwner(ssn = "960522-xxxxxxx")

        val account1 = BankAccount(number = "111-01-xxxxxxx-01")
        val account2 = BankAccount(number = "222-01-xxxxxxx-02")
        val account3 = BankAccount(number = "333-01-xxxxxxx-03")
        val account4 = BankAccount(number = "444-01-xxxxxxx-04")

        owner1.addAccounts(account1, account2)
        owner2.addAccounts(account1, account3, account4)

        accountRepo.save(account1)
        accountRepo.save(account2)
        accountRepo.save(account3)
        accountRepo.save(account4)
        flushAndClear()

        verifyExistsAccount(account1)
        verifyExistsAccount(account2)
        verifyExistsAccount(account3)
        verifyExistsAccount(account4)

        // NOTE: many-to-many 관계를 끊으려면 @JoinTable 를 정의한 entity를 갱신해야 join table에서 관계를 삭제합니다.
        account1.removeOwners(owner2)
        //        accountRepo.save(account1)

        /*
            delete
            from
                account_owner_bank_account_map
            where
                owner_id=?
                and account_id=?
         */
        ownerRepo.save(owner2)
        flushAndClear()

    }

    private fun verifyExistsAccount(account: BankAccount) {
        val loaded = accountRepo.findByIdOrNull(account.id)!!
        loaded shouldEqual account
        loaded.owners.size shouldEqual account.owners.size
    }
}