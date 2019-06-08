package io.github.debop.springdata.jpa.mapping

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import mu.KotlinLogging.logger
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException

class SimpleEntityTests : AbstractDataJpaTest() {

    companion object {
        val log = logger {}
    }

    @Autowired
    lateinit var lifecycleRepo: LifecycleEntityRepository

    @Autowired
    lateinit var simpleRepo: SimpleEntityRepository

    @Test
    fun `transient object equals`() {
        val transient1 = SimpleEntity(name = "transient1")
        val transient2 = SimpleEntity(name = "transient1")

        // name, description 으로
        transient2 shouldEqual transient1

        transient2.description = "added description"
        transient2 shouldEqual transient1

        transient2.name = "other name"
        transient2 shouldNotEqual transient1
    }

    @Test
    fun `unique constraint test`() {
        val entity1 = SimpleEntity(name = "name")
        val entity2 = SimpleEntity(name = "name")
        val entity3 = SimpleEntity(name = "unique name")

        // set 으로 해야 unique 를 처리할 수 있습니다.
        simpleRepo.saveAll(setOf(entity1, entity2, entity3))

        simpleRepo.findAll().size shouldEqual 2

        simpleRepo.deleteAll()

        assertThrows<DataIntegrityViolationException> {
            simpleRepo.saveAll(listOf(SimpleEntity(name = "same"),
                                      SimpleEntity(name = "same")))
        }
    }

    @Test
    fun `entity with lifecycle information`() {

        val saved = lifecycleRepo.save(LifecycleEntity(name = "New Entity"))

        log.debug { "Saved=$saved" }

        saved.id.shouldNotBeNull()
        saved.createdAt.shouldNotBeNull()
        saved.updatedAt.shouldNotBeNull()

        Thread.sleep(100)
        saved.name = "Updated Entity"

        // flush를 하지 않으면 실제로 데이터가 저장된 게 아니기 때문에 같은 속성값을 가지게 된다.
        val updated = lifecycleRepo.saveAndFlush(saved)

        log.debug { "Updated=$updated" }

        updated.updatedAt shouldNotEqual saved.createdAt
    }
}


