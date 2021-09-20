package io.github.debop.springdata.jpa.mapping.embeddable

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Test

/**
 * EmbeddableEntityTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 11
 */
class EmbeddableEntityTest: AbstractDataJpaTest() {

    @Test
    fun `entity has multiple embeddable`() {
        val user = Employee(username = "debop", password = "1234").apply {
            email = "debop"
            homeAddress.city = "Seoul"
            homeAddress.street = "Jungreong"
            homeAddress.zipcode = "02815"
            officeAddress.city = "Seoul"
            officeAddress.street = "Teheran"
            officeAddress.zipcode = "02111"
        }

        val loaded = em.persistFlushFind(user)

        loaded shouldBeEqualTo user
        loaded.homeAddress shouldBeEqualTo user.homeAddress
        loaded.officeAddress shouldBeEqualTo user.officeAddress

        em.remove(loaded)
        flushAndClear()

        em.find(Employee::class.java, user.id).shouldBeNull()
    }
}