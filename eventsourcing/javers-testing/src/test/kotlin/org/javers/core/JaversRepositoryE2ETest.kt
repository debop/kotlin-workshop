package org.javers.core

import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.javers.common.date.DateProvider
import org.javers.core.commit.CommitMetadata
import org.javers.core.model.PrimitiveEntity
import org.javers.core.model.SnapshotEntity
import org.javers.repository.api.JaversRepository
import org.javers.repository.inmemory.InMemoryRepository
import org.javers.repository.jql.QueryBuilder
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

    @Test
    fun `다양한 primitive 수형을 저장합니다`() {
        // GIVEN
        val s = PrimitiveEntity("1")

        // WHEN
        javers.commit("author", s)

        s.intField = 10
        s.longField = 10L
        s.doubleField = 1.1
        s.floatField = 1.1F
        s.charField = 'c'
        s.byteField = 10.toByte()
        s.shortField = 10.toShort()
        s.booleanField = true
        s.IntegerField = 10
        s.LongField = 10
        s.DoubleField = 1.1
        s.FloatField = 1.1F
        s.CharField = 'c'
        s.ByteField = 10.toByte()
        s.ShortField = 10.toShort()
        s.BooleanField = true

        javers.commit("author", s)

        // THEN
        javers.findChanges(QueryBuilder.anyDomainObject().build()).size shouldEqualTo 16
    }
}