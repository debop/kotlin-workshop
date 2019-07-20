package io.github.debop.idgenerators.snowflake

import mu.KLogging
import org.amshove.kluent.shouldBeGreaterOrEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.streams.toList

/**
 * SnowflakeTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 20
 */
class SnowflakeTest {

    companion object: KLogging() {
        const val TEST_COUNT = Snowflake.MAX_SEQUENCE * 4
        val TEST_LIST = (0..TEST_COUNT).toList()
    }

    val snowflake = Snowflake()

    @ParameterizedTest
    @ValueSource(ints = [-100, 0, 100, 1000])
    fun `create Snowflake instance`(machineId: Int) {
        Snowflake(machineId).shouldNotBeNull()
    }

    @Test
    fun `generate snowflake id`() {
        val id1 = snowflake.nextId()
        val id2 = snowflake.nextId()
        id2 shouldBeGreaterThan id1

        logger.debug { "id1=${snowflake.parse(id1)}" }
        logger.debug { "id2=${snowflake.parse(id2)}" }
    }

    @RepeatedTest(5)
    fun `generate snowflake id list`() {
        val ids = snowflake.nextIds(100)
        val sorted = ids.sorted()

        sorted.forEachIndexed { index, id ->
            id shouldEqualTo ids[index]
        }

        ids.toSet().size shouldEqualTo ids.size
    }

    @RepeatedTest(5)
    fun `generate snowflake by nextId as parallel`() {
        val ids = TEST_LIST.parallelStream().map { snowflake.nextId() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }

    @RepeatedTest(5)
    fun `generate snowflake by nextIds`() {
        val ids = snowflake.nextIds(TEST_COUNT)
        ids.toSet().size shouldEqualTo ids.size
    }

    @Test
    fun `generate snowflake by idAsString`() {
        val id1 = snowflake.nextIdAsString()
        val id2 = snowflake.nextIdAsString()

        logger.debug { "id1=$id1" }
        logger.debug { "id2=$id2" }

        (id2 > id1).shouldBeTrue()
    }

    @RepeatedTest(5)
    fun `generate snowflake by nextIdsAsString`() {
        val ids = snowflake.nextIdsAsString(TEST_COUNT)
        val sorted = ids.sorted()

        sorted.forEachIndexed { index, id ->
            id shouldEqual ids[index]
        }

        ids.toSet().size shouldEqualTo ids.size
    }

    @RepeatedTest(5)
    fun `generate idAsString as parallel`() {
        val ids = TEST_LIST.parallelStream().map { snowflake.nextIdAsString() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }

    @Test
    fun `parse snowflake id`() {
        snowflake.nextId()  // for warmup 

        val id1 = snowflake.nextId()
        val id2 = snowflake.nextId()

        val snowflakeId1 = snowflake.parse(id1)
        val snowflakeId2 = snowflake.parse(id2)

        Thread.sleep(1L)
        val id3 = snowflake.nextId()
        val snowflakeId3 = snowflake.parse(id3)

        snowflakeId2.timestamp shouldBeGreaterOrEqualTo snowflakeId1.timestamp
        snowflakeId3.timestamp shouldBeGreaterThan snowflakeId2.timestamp

        snowflakeId1.value shouldEqualTo id1
        snowflakeId2.value shouldEqualTo id2
        snowflakeId3.value shouldEqualTo id3
    }

    @RepeatedTest(5)
    fun `parse snowflake ids as parallel`() {
        val ids = snowflake.nextIds(TEST_COUNT)
        val snowflakeIds = ids.map { snowflake.parse(it) }

        snowflakeIds.size shouldEqualTo ids.size
        snowflakeIds.all { ids.contains(it.value) }.shouldBeTrue()
    }

    @Test
    fun `parse snowflake id as string`() {
        snowflake.nextIdAsString() // warm up

        val id1 = snowflake.nextIdAsString()
        val id2 = snowflake.nextIdAsString()
        Thread.sleep(1L)
        val id3 = snowflake.nextIdAsString()

        val snowflakeId1 = snowflake.parse(id1)
        val snowflakeId2 = snowflake.parse(id2)
        val snowflakeId3 = snowflake.parse(id3)

        snowflakeId2.timestamp shouldBeGreaterOrEqualTo snowflakeId1.timestamp
        snowflakeId3.timestamp shouldBeGreaterThan snowflakeId2.timestamp

        snowflakeId1.valueAsString shouldEqual id1
        snowflakeId2.valueAsString shouldEqual id2
        snowflakeId3.valueAsString shouldEqual id3
    }

    @RepeatedTest(5)
    fun `parse snowflake ids as String as parallel`() {
        val ids = snowflake.nextIdsAsString(TEST_COUNT)
        val snowflakeIds = ids.map { snowflake.parse(it) }

        snowflakeIds.size shouldEqualTo ids.size
        snowflakeIds.all { ids.contains(it.valueAsString) }.shouldBeTrue()
    }
}