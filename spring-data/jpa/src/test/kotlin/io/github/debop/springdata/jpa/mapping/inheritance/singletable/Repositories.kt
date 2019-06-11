package io.github.debop.springdata.jpa.mapping.inheritance.singletable

import org.springframework.data.jpa.repository.JpaRepository


interface SingleTableBankAccountRepository: JpaRepository<BankAccount, Long>

interface SingleTableCreditCardRepository: JpaRepository<CreditCard, Long>