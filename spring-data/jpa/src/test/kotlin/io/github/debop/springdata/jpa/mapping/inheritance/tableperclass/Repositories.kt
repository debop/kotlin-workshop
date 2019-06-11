package io.github.debop.springdata.jpa.mapping.inheritance.tableperclass

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TablePerClassBankAccountRepository: JpaRepository<BankAccount, UUID>

interface TablePerClassCreditCardRepository: JpaRepository<CreditCard, UUID>