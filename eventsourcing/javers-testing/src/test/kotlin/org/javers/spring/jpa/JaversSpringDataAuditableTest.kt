package org.javers.spring.jpa

import mu.KLogging
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.example.JaversSpringJpaApplicationConfig
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.DummyAuditedJpaRepository
import org.javers.spring.repository.DummyAuditedRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.getBean
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * [JaversSpringDataAuditable] annotation이 type이나 method에 적용된 경우 모두 auditable 이 작동한다
 *
 */
@ExtendWith(SpringExtension::class)
class JaversSpringDataAuditableTest {

    companion object : KLogging()

    private lateinit var context: AnnotationConfigApplicationContext
    private lateinit var javers: Javers

    @BeforeAll
    fun setupAll() {
        context = AnnotationConfigApplicationContext(JaversSpringJpaApplicationConfig::class.java)
        javers = context.getBean()
    }

    @Test
    fun `context loading`() {
        javers.shouldNotBeNull()
    }

    @Test
    fun `단순 Repository에 메소드에 @JaversAuditable이 있는 경우에도 감사를 수행합니다`() {
        // GIVEN
        val repository = context.getBean<DummyAuditedRepository>()
        val dummy = DummyObject("some")

        // WHEN
        repository.save(dummy)

        // THEN
        verifyAuditable(dummy)
    }

    @Test
    fun `SpringJpaRepository에 @JaversAuditable이 적용된 경우 감사를 수행합니다`() {
        // GIVEN
        val repository = context.getBean<DummyAuditedJpaRepository>()
        val dummy = DummyObject("some")

        // WHEN
        repository.save(dummy)

        // THEN
        verifyAuditable(dummy)
    }

    private fun verifyAuditable(dummy: DummyObject) {
        val query = QueryBuilder.byInstanceId(dummy.id, DummyObject::class.java).build()
        val snapshots = javers.findSnapshots(query)

        // THEN
        snapshots.size shouldEqualTo 1
    }
}