package org.javers.spring.jpa

import mu.KLogging
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeEqualTo
import org.javers.core.Javers
import org.javers.hibernate.entity.Person
import org.javers.hibernate.entity.PersonCrudRepository
import org.javers.repository.jql.QueryBuilder
import org.javers.repository.sql.JaversSqlRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.sql.DataSource

/**
 * JPA 예외 발생 시 Transaction Rollback 을 수행하고, Javers 내의 commit 도 취소하도록 한다.
 *
 * TODO: 만약 Kafka를 사용할 경우, Kafka 의 transaction을 사용할 것인가? 아니면 compensation message 를 발송할 것인가? 검토 필요
 * TODO: spring-kafka의 TransactionManager와 엮는 방법이 가장 좋을 듯 
 */
@SpringBootTest(classes = [CacheEvictSpringConfig::class])
class CacheEvictTest {

    companion object : KLogging()

    @Autowired
    private lateinit var javers: Javers

    @Autowired
    private lateinit var javersSqlRepository: JaversSqlRepository

    @Autowired
    private lateinit var repository: PersonCrudRepository

    @Autowired
    private lateinit var errorThrowingService: ErrorThrowingService

    @Autowired
    private lateinit var dataSource: DataSource

    @BeforeEach
    fun setup() {
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.execute("DELETE jv_snapshot")
            }
        }
    }

    @Test
    fun `예외 발생으로 rollback 시 Snapshot 저장도 취소되고 GlobalId PK Cache는 제거되어야합니다`() {
        // GIVEN
        val person = Person(id = "debop")

        // WHEN
        repository.save(person)

        val error = assertThrows<RuntimeException> {
            // Update 후 예외를 발생시킨다.
            // 이 때 javersSqlRepository local cache 는 제거된다
            person.name = "Sunghyouk Bae"
            errorThrowingService.saveAndThrow(person)
        }

        // THEN
        error.message shouldBeEqualTo "rollback"

        // update snapshot은 취소된다
        val snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject().build())
        snapshots.size shouldBeEqualTo 1
        logger.debug { "Latest snapshot=\n${javers.jsonConverter.toJson(snapshots[0])}" }
        snapshots[0].getPropertyValue("name").shouldBeNull()

        // GlobalId PK Cache도 clear 된다 
        javersSqlRepository.globalIdPkCacheSize shouldBeEqualTo 0
    }
}