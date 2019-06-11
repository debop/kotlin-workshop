package io.github.debop.springdata.jpa.mapping.associations.manytomany

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountOwnerRepository : JpaRepository<AccountOwner, Long>

@Repository
interface BankAccountRepository : JpaRepository<BankAccount, Long>