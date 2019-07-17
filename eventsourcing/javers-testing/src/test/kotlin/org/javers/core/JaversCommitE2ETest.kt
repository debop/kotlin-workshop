package org.javers.core

import org.amshove.kluent.shouldEqualTo
import org.javers.core.model.CategoryC
import org.javers.core.model.PhoneWithShallowCategory
import org.javers.core.model.ShallowPhone
import org.javers.core.model.SnapshotEntity
import org.javers.repository.cache2k.Cache2kRepository
import org.junit.jupiter.api.Test

/**
 * JaversCommitE2ETest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 15
 */
class JaversCommitE2ETest {

    private fun newJavers(): Javers {
        return JaversBuilder.javers().registerJaversRepository(Cache2kRepository()).build()
    }

    @Test
    fun `ShallowReferenceType 엔티티의 snapshot은 commit하지 않습니다`() {
        // GIVEN
        val javers = newJavers()
        val reference = ShallowPhone(1L, "123", CategoryC(1, "some"))
        val entity = SnapshotEntity(id = 1).apply {
            shallowPhone = reference
            shallowPhones = mutableSetOf(reference)
            shallowPhonesList = mutableListOf(reference)
            shallowPhonesMap = mutableMapOf("key" to reference)
        }

        // WHEN
        val commit = javers.commit("", entity)

        // THEN
        commit.snapshots.forEach { println(it) }
        commit.snapshots.size shouldEqualTo 1
    }

    @Test
    fun `@ShallowReference 가 지정된 property는 snapshot이 commit되지 않습니다`() {
        val javers = newJavers()
        val entity = PhoneWithShallowCategory(1).apply {
            shallowCategory = CategoryC(1, "old shallow")
        }

        val commit = javers.commit("", entity)

        println(commit.snapshots[0])
        commit.snapshots.size shouldEqualTo 1
    }
}