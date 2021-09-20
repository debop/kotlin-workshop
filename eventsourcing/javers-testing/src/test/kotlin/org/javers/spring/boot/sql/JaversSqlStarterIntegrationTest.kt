package org.javers.spring.boot.sql

import mu.KLogging
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.TestApplication
import org.javers.spring.boot.model.DummyEntity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional

/**
 * JaversSqlStarterIntegrationTest
 *
 * @author debop
 * @since 19. 7. 18
 */
@SpringBootTest(classes = [TestApplication::class])
@ActiveProfiles("test")
@Transactional
class JaversSqlStarterIntegrationTest {

    companion object: KLogging()

    @Autowired
    private lateinit var javers: Javers

    @Autowired
    private lateinit var repository: DummyEntityRepository

    @Test
    fun `context loading`() {
        javers.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @Test
    fun `기본 Javers 인스턴스는 auto-audit aspect가 가능합니다`() {
        // WHEN
        val entity = repository.save(DummyEntity.random())
        repository.getOne(entity.id) shouldBeEqualTo entity

        val snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(entity.id, DummyEntity::class.java).build())

        snapshots.size shouldBeEqualTo 1
        with(snapshots[0]) {
            logger.info { "Snapshot=\n${javers.jsonConverter.toJson(this)}" }

            commitMetadata.properties["key"] shouldBeEqualTo "ok"
            commitMetadata.author shouldBeEqualTo "unauthenticated"
        }
    }

    @Test
    fun `컬렉션 저장 시 auto-audit aspect를 호출한다`() {
        // GIVEN:
        val entities = List(10) { DummyEntity.random() }

        // WHEN:
        val persisted = repository.saveAll(entities)

        persisted
            .map { javers.getLatestSnapshot(it.id, DummyEntity::class.java) }
            .forEach { it.isPresent.shouldBeTrue() }
    }
}