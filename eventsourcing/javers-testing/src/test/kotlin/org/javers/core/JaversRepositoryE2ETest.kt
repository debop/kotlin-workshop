package org.javers.core

import mu.KLogging
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeLessOrEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.javers.common.date.DateProvider
import org.javers.core.JaversTestBuilder.Companion.javersTestAssembly
import org.javers.core.commit.CommitMetadata
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.CollectionChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.metamodel.`object`.InstanceId
import org.javers.core.model.ConcreteWithActualType
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.NewEntityWithTypeAlias
import org.javers.core.model.NewValueObjectWithTypeAlias
import org.javers.core.model.PrimitiveEntity
import org.javers.core.model.SnapshotEntity
import org.javers.core.model.SnapshotEntity.DummyEnum
import org.javers.repository.api.JaversRepository
import org.javers.repository.api.SnapshotIdentifier
import org.javers.repository.cache2k.Cache2kRepository
import org.javers.repository.jql.JqlQuery
import org.javers.repository.jql.QueryBuilder
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.EnumSet
import java.util.stream.Stream
import kotlin.math.abs

/**
 * JaversRepositoryE2ETest
 *
 * @author debop
 * @since 19. 7. 15
 */
open class JaversRepositoryE2ETest {

    companion object : KLogging()

    protected lateinit var javers: Javers
    protected lateinit var repository: JaversRepository
    private lateinit var dateProvider: DateProvider
    private var randomCommitGenerator: RandomCommitGenerator? = null

    @BeforeEach
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
        when (val provider = this.dateProvider) {
            is TikDateProvider  -> provider.set(dateTime)
            is FakeDateProvider -> provider.set(dateTime)
        }
    }

    // protected open fun prepareJaversRepository(): JaversRepository = InMemoryRepository()
    protected open fun prepareJaversRepository(): JaversRepository = Cache2kRepository()

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

    @Test
    fun `ValueObject를 소유한 Entity class로부터 ValueObject 변경 조회`() {
        // GIVEN
        val data = listOf(
            DummyUserDetails(1, DummyAddress(city = "London")),
            DummyUserDetails(1, DummyAddress(city = "Paris")),
            SnapshotEntity(1, valueObjectRef = DummyAddress("London")),
            SnapshotEntity(1, valueObjectRef = DummyAddress("Paris")),
            SnapshotEntity(2, valueObjectRef = DummyAddress("Rome")),
            SnapshotEntity(2, valueObjectRef = DummyAddress("Paris")),
            // Noise
            SnapshotEntity(2, valueObjectRef = DummyAddress("Paris")).apply { arrayOfValueObjects = arrayOf(DummyAddress("Luton")) }
        )

        data.forEach {
            javers.commit("author", it)
        }

        // WHEN
        val changes = javers.findChanges(QueryBuilder.byValueObject(SnapshotEntity::class.java, "valueObjectRef").build())

        // THEN
        changes.size shouldEqualTo 2
        commitSeq(changes[0].commitMetadata.get()) shouldEqualTo 6
        changes.forEach {
            it.affectedGlobalId.typeName shouldEqual DummyAddress::class.java.name
        }
    }

    @Test
    fun `JaversRepository로 엔티티의 변화 이력을 저장합니다`() {
        // GIVEN
        val ref = SnapshotEntity(id = 2)
        val cdo = SnapshotEntity(id = 1).apply {
            entityRef = ref
            arrayOfIntegers = intArrayOf(1, 2)
            listOfDates = mutableListOf(LocalDate.of(2001, 1, 1), LocalDate.of(2001, 1, 2))
            mapOfValues[LocalDate.of(2001, 1, 1)] = BigDecimal(1.1)
            mapOfGenericValues["enumSet"] = EnumSet.of(DummyEnum.val1, DummyEnum.val2)
        }

        javers.commit("author", cdo)    // v. 1
        cdo.intProperty = 5
        javers.commit("author2", cdo)    // v. 2

        // WHEN
        val snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).build())

        // THEN
        val refId = GlobalIdTestBuilder.instanceId(2, SnapshotEntity::class.java)

        logger.trace { "refId=$refId" }

        // assert properties
        val snapshot = snapshots.find { (it.globalId as InstanceId).cdoId == 1 }

        logger.trace { "majorId = ${snapshot!!.commitMetadata.id.majorId}" }
        commitSeq(snapshot!!.commitMetadata) shouldEqualTo 2

        snapshot.getPropertyValue("id") shouldEqual 1
        snapshot.getPropertyValue("entityRef") shouldEqual refId
        (snapshot.getPropertyValue("arrayOfIntegers") as IntArray) shouldContainSame intArrayOf(1, 2)

        with(snapshots[0]) {
            commitSeq(commitMetadata) shouldEqualTo 2
            commitMetadata.author shouldEqual "author2"
            changed.size shouldEqualTo 1
            changed[0] shouldEqual "intProperty"
            isInitial.shouldBeFalse()

            logger.debug { "Snapshot commitId: ${this.commitId}" }
            state.forEachProperty { name, value ->
                logger.debug { "property name=$name, value=$value" }
            }
            logger.debug { "Json= ${javers.jsonConverter.toJson(this)}" }
        }

        with(snapshots[1]) {
            commitSeq(commitMetadata) shouldEqualTo 1
            commitMetadata.author shouldEqual "author"
            getPropertyValue("intProperty").shouldBeNull()
            isInitial.shouldBeTrue()

            logger.debug { "Snapshot commitId: ${this.commitId}" }
            state.forEachProperty { name, value ->
                logger.debug { "property name=$name, value=$value" }
            }

            logger.debug { "Json= ${javers.jsonConverter.toJson(this)}" }
        }

        val changes = javers.findChanges(QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).build())

        logger.debug { "Changes=${changes.prettyPrint()}" }
        logger.debug { "Changes Json = ${javers.jsonConverter.toJson(changes)}" }
    }

    @Test
    fun `엔티티 속성을 Repository의 가장 최신 것과 비교하기`() {

        //GIVEN
        val user = DummyUser(name = "John").apply { age = 18 }
        javers.commit("login", user)

        // WHEN
        user.age = 19
        javers.commit("login", user)

        val history = javers.findChanges(QueryBuilder.byInstanceId("John", DummyUser::class.java).build())

        // THEN
        with(history[0]) {

            logger.trace { "change=${javers.jsonConverter.toJson(this)}" }

            this shouldBeInstanceOf ValueChange::class.java
            affectedGlobalId shouldEqual GlobalIdTestBuilder.instanceId("John", DummyUser::class.java)

            with(this as ValueChange) {
                propertyName shouldEqual "age"
                left shouldEqual 18
                right shouldEqual 19
            }
        }
    }

    @Test
    fun `Repository로부터 Shadow를 꺼내온다`() {
        //GIVEN
        val user = DummyUser(name = "John").apply { age = 18 }
        javers.commit("login", user)

        // WHEN
        user.age = 19
        javers.commit("login", user)

        // Shadows는 저장된 Snapshot 변경이력으로부터, 원하는 객체 재구성해준다
        val shadows = javers.findShadows<DummyUser>(QueryBuilder.byInstance(user).build())

        // THEN
        shadows.size shouldEqualTo 2

        val newUser = shadows[0].get()
        val oldUser = shadows[1].get()

        newUser.age shouldEqualTo 19
        oldUser.age shouldEqualTo 18

        shadows[0].commitMetadata.id.majorId shouldEqualTo 2
        shadows[1].commitMetadata.id.majorId shouldEqualTo 1
    }

    @Test
    fun `Repository에 snapshots을 연속으로 commit 하고 read 하기`() {
        val cdo = SnapshotEntity(1)

        (1..25).forEach {
            cdo.intProperty = it
            javers.commit("login", cdo)
            val snap = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).build()).first()
            snap.getPropertyValue("intProperty") shouldEqual it
        }
    }

    @Test
    fun `부모클래스의 Generric 필드에 대한 변화를 commit 하기`() {
        // GIVEN
        javers.commit("author", ConcreteWithActualType("a", listOf("1")))
        javers.commit("author", ConcreteWithActualType("a", listOf("1", "2")))

        // WHEN
        val changes = javers.findChanges(QueryBuilder.byClass(ConcreteWithActualType::class.java).build())

        // THEN
        val change = changes[0]

        Assumptions.assumeTrue { change is CollectionChange }
        change shouldBeInstanceOf CollectionChange::class.java
        if (change is CollectionChange) {
            val elementChange = change.changes[0]
            elementChange shouldBeInstanceOf ValueAdded::class.java

            if (elementChange is ValueAdded) {
                elementChange.index shouldEqualTo 1
                elementChange.addedValue shouldBeInstanceOf String::class
                elementChange.addedValue shouldEqual "2"
            }
        }
    }

    @Test
    fun `ValueObject와 소유권자인 Entity 를 ValueObject 쿼리로 조회하기`() {
        // GIVEN
        javers.commit("author", NewEntityWithTypeAlias(1.toBigDecimal()).apply { valueObject = NewValueObjectWithTypeAlias(5) })
        javers.commit("author", NewEntityWithTypeAlias(1.toBigDecimal()).apply { valueObject = NewValueObjectWithTypeAlias(6) })

        // WHEN
        val changes = javers.findChanges(QueryBuilder
                                             .byValueObject(NewEntityWithTypeAlias::class.java, "valueObject")
                                             .build())

        // THEN
        changes.size shouldEqualTo 1

        val valueChange = changes.find { it is ValueChange && it.propertyName == "some" } as ValueChange

        valueChange.left shouldEqual 5
        valueChange.right shouldEqual 6
    }

    @ParameterizedTest
    @MethodSource("getTimeRangeQuery")
    fun `Entity Snapshot을 기간으로 검색하기`(query: JqlQuery, expectedCommitDates: List<LocalDateTime>) {
        // GIVEN:
        repeat(5) {
            val index = it + 1
            val entity = SnapshotEntity(1).apply { intProperty = index }
            val now = ZonedDateTime.of(2015, 1, 1, index, 0, 0, 0, ZoneOffset.UTC)
            setNow(now)
            javers.commit("author", entity)
        }

        // WHEN
        val snapshots = javers.findSnapshots(query)
        val commitDates = snapshots.map { it.commitMetadata.commitDate }

        // THEN
        commitDates shouldContainSame expectedCommitDates
    }

    private fun getTimeRangeQuery(): Stream<Arguments> =
        Stream.of(
            Arguments.of(
                QueryBuilder.byInstanceId(1, SnapshotEntity::class.java)
                    .from(LocalDateTime.of(2015, 1, 1, 3, 0))
                    .build(),
                (5 downTo 3).map { LocalDateTime.of(2015, 1, 1, it, 0) }
            ),
            Arguments.of(
                QueryBuilder.byInstanceId(1, SnapshotEntity::class.java)
                    .to(LocalDateTime.of(2015, 1, 1, 3, 0))
                    .build(),
                (3 downTo 1).map { LocalDateTime.of(2015, 1, 1, it, 0) }
            ),

            Arguments.of(
                QueryBuilder
                    .byInstanceId(1, SnapshotEntity::class.java)
                    .from(LocalDateTime.of(2015, 1, 1, 2, 0))
                    .to(LocalDateTime.of(2015, 1, 1, 4, 0))
                    .build(),
                (4 downTo 2).map { LocalDateTime.of(2015, 1, 1, it, 0) }
            )
        )

    @Test
    fun `Entity snapshot version 은 증가됩니다`() {
        // WHEN
        javers.commit("author", SnapshotEntity(1).apply { intProperty = 11 })
        javers.commit("author", SnapshotEntity(1).apply { intProperty = 22 })
        javers.commit("author", SnapshotEntity(1).apply { intProperty = 33 })

        // THEN
        val snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).build())
        snapshots[0].version shouldEqualTo 3
        snapshots[1].version shouldEqualTo 2
        snapshots[2].version shouldEqualTo 1
    }


    @RepeatedTest(3)
    fun `200개의 다른 snapshot들을 조회한다`() {
        // GIVEN:
        repeat(200) {
            javers.commit("author", SnapshotEntity(id = 1).apply { intProperty = it + 1 })
        }

        val instanceId = javersTestAssembly().instanceId(SnapshotEntity(id = 1))
        val snapshotIdentifiers = List(200) { SnapshotIdentifier(instanceId, it + 1L) }

        // WHEN:
        val snapshots = repository.getSnapshots(snapshotIdentifiers)

        // THEN:
        snapshots.size shouldEqualTo snapshotIdentifiers.size
    }

    @Test
    fun `Commited properties 를 commit 하고 read 하기`() {
        val commitProperties = mapOf(
            "tenant" to "ACME",
            "sessionId" to "1234567890",
            "device" to "smartwatch",
            "yet another property name" to "yet another property value"
        )
        javers.commit("author", SnapshotEntity(id = 1), commitProperties)

        // WHEN
        val snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).build()).first()

        // THEN
        snapshot.commitMetadata.properties shouldContainSame commitProperties
    }

    @Test
    fun `cluster-friendly commitId generator 지원하기`() {
        val threads = 10
        val javersRepo = Cache2kRepository()

        List(10) { it }
            .parallelStream()
            .map {
                it to JaversBuilder.javers()
                    .registerJaversRepository(javersRepo)
                    .withCommitIdGenerator(CommitIdGenerator.RANDOM)
                    .build()
            }.forEach {
                val commit = it.second.commit("author", SnapshotEntity(id = it.first))
                logger.debug { "Commit entity. commit=$commit" }
            }

        val javers = JaversBuilder.javers().registerJaversRepository(javersRepo).build()
        val snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject().build())

        snapshots.map { it.commitId }.toSet().size shouldEqualTo threads
    }

    @Test
    fun `변경이 없는 zero snapshots 은 commit 되지 않습니다`() {
        // GIVEN
        val anEntity = SnapshotEntity(1).apply { intProperty = 100 }

        // WHEN
        val commit = javers.commit("author", anEntity)
        val snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity::class.java).build())

        // THEN
        snapshots.size shouldEqualTo 1
        repository.headId shouldEqual commit.id

        // WHEN: 변경이 없는 엔티티는 저장되면 안됩니다
        javers.commit("author", anEntity)

        // THEN: 저장되지 않았으므로 headId의 변화가 없다 
        repository.headId shouldEqual commit.id
    }
}