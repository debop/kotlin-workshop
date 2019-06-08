package io.github.debop.springdata.jpa.mapping.onetoone

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import javax.persistence.PersistenceException

/**
 * UserTests
 * @author debop (Sunghyouk Bae)
 */
class UserTests : AbstractDataJpaTest() {

    @Autowired
    private lateinit var userRepo: OneToOneUserRepository

    @Autowired
    private lateinit var addrRepo: OneToOneAddressRepository

    @Test
    fun `unidirectional one-to-one, address has ownership`() {

        val user = User(username = "debop")
        val addr = Address(street = "street", city = "city", zipcode = "zipcode")
        user.shippingAddress = addr

        // cascade 도 없고, Address가 독립적이므로, 따로 저장해주어야 한다.
        addrRepo.save(addr)
        userRepo.save(user)
        flushAndClear()

        val user2 = userRepo.findByIdOrNull(user.id)!!
        user2 shouldEqual user
        user2.shippingAddress shouldEqual addr

        userRepo.delete(user2)
        flushAndClear()

        // user 는 삭제되었지만, cascade 가 없으므로, addr 는 살아있다
        userRepo.existsById(user2.id!!).shouldBeFalse()
        addrRepo.existsById(addr.id!!).shouldBeTrue()

        val addr2 = addrRepo.findByIdOrNull(addr.id)!!

        val newUser = User(username = "new-user")
        newUser.shippingAddress = addr2
        userRepo.save(newUser)
        flushAndClear()

        // user 가 address 를 참조하고 있으므로 삭제할 수 없다
        assertThrows<PersistenceException> {
            addrRepo.delete(newUser.shippingAddress!!)
            flushAndClear()
        }
    }
}