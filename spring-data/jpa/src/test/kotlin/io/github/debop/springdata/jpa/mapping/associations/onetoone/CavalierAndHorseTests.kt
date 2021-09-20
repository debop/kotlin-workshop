package io.github.debop.springdata.jpa.mapping.associations.onetoone

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import mu.KotlinLogging.logger
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

/**
 * TODO: User and Address 관계와 유사하지만 미묘한 차이가 있다. 비교해서 차이점을 알아보자 (ForeignKey 부분)
 */
class CavalierAndHorseTests : AbstractDataJpaTest() {

    private val log = logger {}

    @Autowired
    private lateinit var cavalierRepo: CavalierRepository

    @Autowired
    private lateinit var horseRepo: HorseRepository

    @Test
    fun `unidirectional one to one, horse has ownership - manytoone 과 유사`() {
        val horse = Horse("적토마")
        val cavalier = Cavalier("관우", horse)

        // cascade=ALL 이므로 cavalier만 저장하면 된다
        cavalierRepo.save(cavalier)
        flushAndClear()

        val cavalier2 = cavalierRepo.findByIdOrNull(cavalier.id)!!
        cavalier2 shouldBeEqualTo cavalier
        cavalier2.horse shouldBeEqualTo horse

        val horse2 = cavalier2.horse!!
        horseRepo.delete(horse2)
        horseRepo.flush()

        // 실제 삭제되는 것이 아니라 cavalier2.hource = null 로 설정될 뿐이다.
        horseRepo.existsById(horse2.id!!).shouldBeTrue()

        cavalierRepo.delete(cavalier2)
        flushAndClear()

        cavalierRepo.existsById(cavalier2.id!!).shouldBeFalse()
        horseRepo.existsById(horse2.id!!).shouldBeFalse()
    }
}