package org.javers.core

import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldEqual
import org.javers.common.date.DateProvider
import org.javers.core.commit.CommitMetadata
import org.javers.core.model.SnapshotEntity
import org.javers.repository.api.JaversRepository
import org.javers.repository.inmemory.InMemoryRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/**
 * JaversRepositoryE2ETest
 *
 * @author debop
 * @since 19. 7. 15
 */
class JaversRepositoryE2ETest {

    protected lateinit var javers: Javers
    protected lateinit var repository: JaversRepository
    private lateinit var dateProvider: DateProvider
    private var randomCommitGenerator: RandomCommitGenerator? = null

    @BeforeAll
    fun setup() {
        buildJaversInstance()
    }

    protected open fun buildJaversInstance() {

        dateProvider = prepareDateProvider()
        repository = prepareJaversRepository()

        val javersBuilder = JaversBuilder
            .javers()
            .withDateTimeProvider(dateProvider)
            .registerJaversRepository(repository)

        if (useRandomCommitIdGenerator()) {
            randomCommitGenerator = RandomCommitGenerator()
            javersBuilder.withCustomCommitIdGenerator(randomCommitGenerator)
        }

        javers = javersBuilder.build()
    }

    protected open fun commitSeq(commit: CommitMetadata): Int {
        if (useRandomCommitIdGenerator()) {
            return randomCommitGenerator!!.getSeq(commit.id)
        }
        return commit.id.majorId.toInt()
    }

    protected open fun prepareDateProvider(): DateProvider {
        if (useRandomCommitIdGenerator()) {
            return TikDateProvider()
        }
        return FakeDateProvider()
    }

    protected open fun setNow(dateTime: ZonedDateTime) {
        val provider = this.dateProvider
        when (provider) {
            is TikDateProvider -> provider.set(dateTime)
            is FakeDateProvider -> provider.set(dateTime)
        }
    }

    protected open fun prepareJaversRepository(): JaversRepository = InMemoryRepository()

    protected open fun useRandomCommitIdGenerator(): Boolean = false


    @Test
    fun `CommitMetadata에 현재 LocalDateTime 과 Instant 정보를 저장한다`() {

        // GIVEN
        val now = ZonedDateTime.now()
        setNow(now)

        // WHEN
        javers.commit("author", SnapshotEntity(1))
        val snapshot = javers.getLatestSnapshot(1, SnapshotEntity::class.java).get()

        // THEN:
        abs(ChronoUnit.MILLIS.between(snapshot.commitMetadata.commitDate, now.toLocalDateTime())) shouldBeLessOrEqualTo 1
        snapshot.commitMetadata.commitDateInstant shouldEqual now.toInstant()
        snapshot.commitMetadata.author shouldEqual "author"
    }
}