package io.github.debop.uuid.snowflake

import mu.KLogging
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

    companion object: KLogging()

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
    fun `generate id as parallel`() {
        val ids = (0 until 100).toList().parallelStream().map { snowflake.nextId() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }

    @Test
    fun `generate snowflake idAsString`() {
        val id1 = snowflake.nextIdAsString()
        val id2 = snowflake.nextIdAsString()

        logger.debug { "id1=$id1" }
        logger.debug { "id2=$id2" }

        (id2 > id1).shouldBeTrue()
    }

    @RepeatedTest(5)
    fun `generate snowflake idsAsString as string`() {
        val ids = snowflake.nextIdsAsString(100)
        val sorted = ids.sorted()

        sorted.forEachIndexed { index, id ->
            id shouldEqual ids[index]
        }

        ids.toSet().size shouldEqualTo ids.size
    }

    @RepeatedTest(5)
    fun `generate idAsString as parallel`() {
        val ids = (0 until 100).toList().parallelStream().map { snowflake.nextIdAsString() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }
}