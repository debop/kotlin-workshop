package io.github.debop.springdata.jpa.mapping.onetoone

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import mu.KotlinLogging.logger
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

/**
 * CarAndWheelTests
 * @author debop (Sunghyouk Bae)
 */
class CarAndWheelTests : AbstractDataJpaTest() {

    private val log = logger {}

    @Autowired
    private lateinit var carRepo: OneToOneCarRepository

    @Autowired
    private lateinit var wheelRepo: OneToOneWheelRepository

    @Test
    fun `unidirectional one to one`() {
        val car = Car("BMW")
        val wheel = Wheel("18-inch").also { it.diameter = 18.0 }
        wheel.car = car

        // em.persist(car)  // OneToOne cascade = PERSIST 이므로 자동으로 호출된다.
        em.persist(wheel)
        flushAndClear()

        val wheel2 = wheelRepo.findByIdOrNull(wheel.id)!!
        wheel2 shouldEqual wheel
        wheel2.car shouldEqual car

        wheelRepo.delete(wheel2)
        carRepo.delete(wheel2.car!!)
        flushAndClear()

        carRepo.existsById(car.id!!).shouldBeFalse()
        wheelRepo.existsById(wheel2.id!!).shouldBeFalse()
    }
}