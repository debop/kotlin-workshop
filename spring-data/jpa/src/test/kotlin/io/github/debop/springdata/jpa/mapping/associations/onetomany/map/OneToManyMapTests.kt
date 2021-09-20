package io.github.debop.springdata.jpa.mapping.associations.onetomany.map

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

/**
 * OneToManyMapTests
 *
 * @author debop
 * @since 19. 6. 5
 */
class OneToManyMapTests : AbstractDataJpaTest() {

    @Autowired
    private lateinit var carRepo: CarRepository

    @Autowired
    private lateinit var carPartRepo: CarPartRepository

    @Test
    fun `context loading`() {
        carRepo.shouldNotBeNull()
    }

    @Test
    fun `one-to-many with embeddable mapped by @CollectionTable `() {
        val car = Car(name = "BMW")
        val option1 = CarOption("Navi")
        val option2 = CarOption("Audio")
        val option3 = CarOption("Wheel")

        car.options[option1.name] = option1
        car.options[option2.name] = option2
        car.options[option3.name] = option3

        carRepo.saveAndFlush(car)
        clear()

        val loaded = carRepo.findByIdOrNull(car.id)
        loaded.shouldNotBeNull()
        loaded shouldBeEqualTo car
        loaded.options.size shouldBeEqualTo car.options.size
        loaded.options.values shouldContainAll listOf(option1, option2, option3)

        loaded.options.remove("Audio")
        carRepo.saveAndFlush(loaded)
        clear()

        val loaded2 = carRepo.findByIdOrNull(car.id)
        loaded2.shouldNotBeNull()
        loaded2 shouldBeEqualTo car
        loaded2.options.size shouldBeEqualTo car.options.size - 1
        loaded2.options.values shouldContainAll listOf(option1, option3)
        loaded2.options.values shouldNotContain option2

        carRepo.deleteById(loaded2.id!!)
        carRepo.flush()
        clear()

        carRepo.existsById(loaded2.id!!).shouldBeFalse()
    }

    @Test
    fun `one-to-many with entity`() {
        val car = Car(name = "BMW")
        val engine = CarPart(name = "Engine-B40")
        val wheel = CarPart(name = "Wheel-17inch")
        val mission = CarPart(name = "Mission-ZF8")

        car.parts["Engine"] = engine
        car.parts["Wheel"] = wheel
        car.parts["Mission"] = mission

        // cascade 가 없으므로 독자적으로 저장해야 한다.
        carPartRepo.saveAll(listOf(engine, wheel, mission))
        carRepo.saveAndFlush(car)
        clear()

        val loaded = carRepo.findByIdOrNull(car.id)!!
        loaded shouldBeEqualTo car
        loaded.parts.size shouldBeEqualTo car.parts.size
        loaded.parts.values shouldContainAll listOf(engine, wheel, mission)

        carRepo.deleteById(loaded.id!!)
        carRepo.flush()

        carRepo.existsById(car.id!!).shouldBeFalse()

        // cascade 가 없으므로 car part 는 삭제되지 않음. join table 에서만 삭제됩니다.
        carPartRepo.findAll().shouldNotBeEmpty()
    }
}