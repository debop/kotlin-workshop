package io.github.debop.springdata.jpa.mapping.compositeid

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

class CompositeIdTest: AbstractDataJpaTest() {

    @Test
    fun `composite id with multiple @Id`() {
        val car = IdClassCar("BMW", 2015)
        car.serialNo = "3956"

        val loaded = em.persistFlushFind(car)

        loaded.shouldNotBeNull()
        loaded shouldBeEqualTo car

        loaded.brand shouldBeEqualTo car.brand
        loaded.year shouldBeEqualTo car.year

        clear()

        val loaded2 = em.find(IdClassCar::class.java, CarIdentifier("BMW", 2015))
        loaded2 shouldBeEqualTo car
    }

    @Test
    fun `composite id with embedded identifier`() {

        val car = EmbeddedIdCar(EmbeddableCarId("Kia", 2012))
        car.serialNo = "6675"

        val loaded = em.persistFlushFind(car)

        loaded.shouldNotBeNull()
        loaded shouldBeEqualTo car

        loaded.id.brand shouldBeEqualTo car.id.brand
        loaded.id.year shouldBeEqualTo car.id.year

        clear()

        val loaded2 = em.find(EmbeddedIdCar::class.java, EmbeddableCarId("Kia", 2012))
        loaded2 shouldBeEqualTo car
    }
}