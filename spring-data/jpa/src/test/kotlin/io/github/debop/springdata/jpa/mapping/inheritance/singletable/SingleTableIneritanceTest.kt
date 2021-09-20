package io.github.debop.springdata.jpa.mapping.inheritance.singletable

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.util.Date

class SingleTableIneritanceTest: AbstractDataJpaTest() {

    @Autowired
    private lateinit var accountRepo: SingleTableBankAccountRepository

    @Autowired
    private lateinit var cardRepo: SingleTableCreditCardRepository

    @Test
    fun `single table with two entities`() {

        val account = BankAccount("debop", "123-account")
        account.bankname = "KB"
        account.swift = "kb-swift"

        em.persist(account)

        val card = CreditCard("debop", "1111-2222-3333-4444")
        card.companyName = "KakaoBank"
        card.expYear = 2020
        card.expMonth = 12
        card.startDate = Date()

        em.persist(card)

        flushAndClear()

        val account2 = accountRepo.findByIdOrNull(account.id)!!
        account2 shouldBeEqualTo account

        val card2 = cardRepo.findByIdOrNull(card.id)!!
        card2 shouldBeEqualTo card

        accountRepo.deleteAll()
        accountRepo.flush()

        // 같은 테이블이지만, bank account 정보가 삭제되어도 credit card 정보는 남아 있어야 한다.
        cardRepo.findAll().shouldNotBeEmpty()

        cardRepo.deleteAll()
        flushAndClear()
        cardRepo.findAll().shouldBeEmpty()

    }
}