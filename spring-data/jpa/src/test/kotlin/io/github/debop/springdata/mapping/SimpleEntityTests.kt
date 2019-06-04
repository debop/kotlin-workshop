package io.github.debop.springdata.mapping

import io.github.debop.springdata.AbstractDataJpaTest
import mu.KotlinLogging.logger
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

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

interface SimpleEntityRepository : JpaRepository<SimpleEntity, Long>

interface LifecycleEntityRepository : JpaRepository<LifecycleEntity, Long>

@Entity(name = "simple_simple_entity")
data class SimpleEntity(
    @Id
    @Column(name = "simple_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String
) {
    // equals 에서 빼기 위해
    var description: String? = null
}

// entity audit을 하려면 `@EntityListener` 를 추가해주어야 한다.
@EntityListeners(AuditingEntityListener::class)
@Entity(name = "simple_lifecycle_entity")
data class LifecycleEntity(
    @Id
    @Column(name = "simple_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(name = "createdAt", updatable = false)
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @Column(name = "updatedAt", insertable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)

