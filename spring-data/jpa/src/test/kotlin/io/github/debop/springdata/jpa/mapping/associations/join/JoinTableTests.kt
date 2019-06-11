package io.github.debop.springdata.jpa.mapping.associations.join

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class JoinTableTests @Autowired constructor(
    private val userRepo: JoinUserRepository,
    private val customerRepo: JoinCustomerRepository) : AbstractDataJpaTest() {

    @Test
    fun `create user with address by join table`() {
        val user = JoinUser(name = "debop").apply {
            addresses["Home"] = AddressEntity(city = "Hanam")
            addresses["Office"] = AddressEntity(city = "Seoul")

            nicknames.add("debop68")
        }

        userRepo.saveAndFlush(user)
        clear()

        val loaded = userRepo.findByIdOrNull(user.id)
        loaded.shouldNotBeNull()
        loaded.addresses.size shouldEqualTo 2

        loaded.addresses.remove("Office")
        userRepo.saveAndFlush(loaded)

        loaded.addresses.size shouldEqualTo 1
    }

    @Test
    fun `create customer with embeddable address`() {
        val customer = JoinCustomer(name = "debop").apply {

            // join table with subselect fetch
            addresses["Home"] = AddressEntity(city = "Hanam")
            addresses["Office"] = AddressEntity(city = "Seoul")

            // embeddable
            address.street = "570"
            address.city = "Seoul"
        }

        customerRepo.saveAndFlush(customer)
        clear()

        val loaded = customerRepo.findByIdOrNull(customer.id)
        loaded.shouldNotBeNull()
        loaded.addresses.size shouldEqualTo 2

        loaded.addresses.remove("Office")
        customerRepo.saveAndFlush(loaded)

        loaded.addresses.size shouldEqualTo 1
    }
}