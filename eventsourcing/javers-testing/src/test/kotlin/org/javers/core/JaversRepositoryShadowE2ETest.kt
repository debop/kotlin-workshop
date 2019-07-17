package org.javers.core

import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.commit.CommitId
import org.javers.core.model.CategoryC
import org.javers.core.model.ShallowPhone
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.streams.toList

/**
 * JaversRepositoryShadowE2ETest
 *
 * @author debop
 * @since 19. 7. 17
 */
class JaversRepositoryShadowE2ETest : JaversRepositoryE2ETest() {

    @Test
    fun `stream query 를 반환한다`() {
        // GIVEN
        val entity = SnapshotEntity(1).apply { intProperty = 1 }
        javers.commit("a", entity)
        entity.intProperty = 2
        javers.commit("a", entity)

        // WHEN
        val query = QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).build()
        val shadows = javers.findShadowsAndStream<SnapshotEntity>(query).map { it.get() }.toList()

        // THEN
        shadows.size shouldEqualTo 2
        with(shadows[0] as SnapshotEntity) {
            id shouldEqualTo 1
            intProperty shouldEqual 2
        }
        with(shadows[1] as SnapshotEntity) {
            id shouldEqualTo 1
            intProperty shouldEqual 1
        }
    }

    @Test
    fun `shadow 쿼리 시 지연된 결과 반환을 수행`() {
        // GIVEN
        val entity = SnapshotEntity(1).apply { intProperty = 1 }
        repeat(20) {
            entity.intProperty = it
            javers.commit("a", entity)
        }

        // WHEN
        val query = QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).limit(5).build()
        val shadows = javers.findShadowsAndStream<SnapshotEntity>(query)
            .limit(12)
            .toList()

        // THEN

        shadows.size shouldEqual 12
        repeat(12) {
            commitSeq(shadows[it].commitMetadata) shouldEqual 20 - it
            shadows[it].get().id shouldEqualTo 1
            shadows[it].get().intProperty shouldEqual 19 - it
        }

        // 쿼리 통계도 지원한다
        query.stats().dbQueriesCount shouldEqualTo 1
        query.stats().allSnapshotsCount shouldEqualTo 5
        query.stats().shallowSnapshotsCount shouldEqualTo 5
    }

    @Test
    fun `query stream 이 skip 이 안될 때는 예외를 발생시킵니다`() {
        val error = assertThrows<JaversException> {
            javers.findShadowsAndStream<SnapshotEntity>(QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).skip(5).build())
        }
        error.code shouldEqual JaversExceptionCode.MALFORMED_JQL
    }

    @Test
    fun `Stream 조회한 entity를 재사용하기`() {
        // GIVEN
        val ref = SnapshotEntity(id = 2)
        javers.commit("a", ref)

        val e = SnapshotEntity(id = 1, entityRef = ref)
        repeat(15) {
            e.intProperty = it
            javers.commit("a", e)
        }

        // WHEN
        val query = QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).limit(5).withScopeDeepPlus(1).build()
        val shadows = javers.findShadowsAndStream<SnapshotEntity>(query).map { it.get() }.toList()

        // THEN
        shadows.size shouldEqualTo 15
        shadows.first().intProperty shouldEqual 14
        shadows.last().intProperty shouldEqual 0

        shadows.forEach {
            it.entityRef?.id ?: -1 shouldEqualTo 2
        }
    }

    @Test
    fun `존재하지 않는 commitId를 조회하면 아무것도 반환하지 않습니다`() {
        // GIVEN
        val ref1 = SnapshotEntity(id = 2)
        val ref2 = SnapshotEntity(id = 3)
        javers.commit("a", ref1)
        javers.commit("a", ref2)

        val entity = SnapshotEntity(id = 1).apply { listOfEntities.addAll(listOf(ref1, ref2)) }
        javers.commit("a", entity)

        // WHEN
        val query = QueryBuilder
            .byInstanceId(1, SnapshotEntity::class.java)
            .withScopeDeepPlus(1)
            .withCommitId(CommitId.valueOf("543434.0")) // non-existing commitId
            .build()

        val snapshots = javers.findSnapshots(query)
        val shadows = javers.findShadows<SnapshotEntity>(query)

        snapshots.shouldBeEmpty()
        shadows.shouldBeEmpty()
    }

    @Test
    fun `COMMIT_DEEP scope와 DEEP_PLUS scope를 같이 쓰면 안됩니다`() {
        val eRef = SnapshotEntity(id = 2).apply { intProperty = 2 }
        val e = SnapshotEntity(id = 1).apply { intProperty = 1; entityRef = eRef }
        javers.commit("a", e)

        e.intProperty = 30
        eRef.intProperty = 3
        javers.commit("a", eRef)
        javers.commit("a", e)

        e.intProperty = 33
        eRef.intProperty = 4
        javers.commit("a", eRef)
        javers.commit("a", e)

        // WHEN
        assertThrows<JaversException> {
            val query = QueryBuilder.byInstance(e).withScopeDeepPlus().withScopeCommitDeep().build()
            val shadows = javers.findShadows<SnapshotEntity>(query)
        }

        val query = QueryBuilder.byInstance(e).withScopeDeepPlus().build()
        val shadows = javers.findShadows<SnapshotEntity>(query)

        with(shadows[0].get()) { entityRef?.intProperty shouldEqual 4 }
        with(shadows[1].get()) { entityRef?.intProperty shouldEqual 3 }
        with(shadows[2].get()) { entityRef?.intProperty shouldEqual 2 }
    }

    @Test
    fun `DEEP_PLUS scope에서는 refs 를 prefetch 합니다`() {
        // GIVEN
        val ref = SnapshotEntity(id = 2)
        val entity = SnapshotEntity(id = 1, entityRef = ref)

        repeat(4) {
            ref.intProperty = it
            entity.intProperty = it
            javers.commit("a", ref)
            javers.commit("a", entity)
        }

        // WHEN
        val query = QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).withScopeDeepPlus().build()
        val shadows = javers.findShadows<SnapshotEntity>(query).map { it.get() }

        // THEN
        with(query.stats()) {
            dbQueriesCount shouldEqualTo 2
            allSnapshotsCount shouldEqualTo 8
            deepPlusSnapshotsCount shouldEqual 4
        }

        repeat(4) {
            with(shadows[it]) {
                intProperty shouldEqual 3 - it
                entityRef?.intProperty shouldEqual 3 - it
            }
        }
    }

    @Test
    fun `ShallowReferenceType 엔티티의 thin shadows 를 로드합니다`() {
        // GIVEN
        // ShallowPhone 은 `@ShallowReference` 가 적용되어 로드하지 않는다
        val reference = ShallowPhone(id = 2L, number = "123", category = CategoryC(2, "some"))
        val entity = SnapshotEntity(1).apply {
            shallowPhone = reference
            shallowPhones = mutableSetOf(reference)
            shallowPhonesList = mutableListOf(reference)
            shallowPhonesMap = mutableMapOf("key" to reference)
        }

        // entity만 commit 하고, reference는 commit하지 않는다
        javers.commit("a", entity)

        // WHEN
        val query = QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).withScopeDeepPlus().build()
        val shadows = javers.findShadows<SnapshotEntity>(query).map { it.get() }

        // THEN
        logger.debug { "shadow=${shadows.first()}" }
        shadows.size shouldEqualTo 1

        assertThinShadowOfPhone(shadows.first().shallowPhone)
        assertThinShadowOfPhone(shadows.first().shallowPhones.first())
        assertThinShadowOfPhone(shadows.first().shallowPhonesList.first())
        assertThinShadowOfPhone(shadows.first().shallowPhonesMap["key"])

        // WHEN
        javers.commit("a", reference)
        javers.commit("a", entity)

        val shadows2 = javers.findShadows<SnapshotEntity>(query).map { it.get() }
        shadows2.size shouldEqualTo 1

        // THEN
        logger.debug { "shadow2 = ${shadows2.first()}" }
        assertThinShadowOfPhone(shadows2.first().shallowPhone)
        assertThinShadowOfPhone(shadows2.first().shallowPhones.first())
        assertThinShadowOfPhone(shadows2.first().shallowPhonesList.first())
        assertThinShadowOfPhone(shadows2.first().shallowPhonesMap["key"])
    }

    private fun assertThinShadowOfPhone(shadow: Any?) {
        shadow.shouldNotBeNull()
        shadow shouldBeInstanceOf ShallowPhone::class.java

        if (shadow is ShallowPhone) {
            shadow.id shouldEqualTo 2L
            shadow.number.shouldBeNull()
            shadow.category.shouldBeNull()
        }
    }
}