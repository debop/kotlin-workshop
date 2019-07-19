package io.github.debop.uuid.snowflake

import java.lang.Character.MAX_RADIX
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock

/**
 * Snowflake
 *
 * @param machineId machine or processor number. number range is [0, 64)
 */
class Snowflake(machineId: Int = 0) {

    companion object {
        const val EPOCH = 1420045200000L
        const val MAX_MACHINE_ID = 64
        const val ALPHA_NUMERIC_BASE = MAX_RADIX
        const val TIME_STAMP_SHIFT = 22
        const val MACHINE_ID_SHIFT = 16
        const val MAX_INCREMENT = 16382

        internal fun makeId(timestamp: Long, machineId: Int, increment: Int) =
            (timestamp shl TIME_STAMP_SHIFT) or
            (machineId shl MACHINE_ID_SHIFT).toLong() or
            increment.toLong()
    }

    /**
     * Machine or process ID
     */
    val machineId = machineId % MAX_MACHINE_ID

    private var increment = AtomicInteger(0)
    private val syncObj = ReentrantLock()


    /**
     * Generate next Snowflake ID
     *
     * @return snowflake id
     */
    fun nextId(): Long {
        val timestamp = System.currentTimeMillis() - EPOCH
        increment.compareAndSet(MAX_INCREMENT - 1, 0)
        return makeId(timestamp, machineId, increment.incrementAndGet())
    }

    /**
     * Generate snowflake ids
     *
     * @param size size of snowflake ids
     * @return collection of snowflake ids
     */
    fun nextIds(size: Int): List<Long> {
        return List(size) { nextId() }
    }

    /**
     * Generate Snowflake ID as String
     *
     * @return snowflake id
     */
    fun nextIdAsString(): String {
        return nextId().toString(ALPHA_NUMERIC_BASE)
    }

    /**
     * Generate snowflake ids as String
     *
     * @param size size of ids
     * @return collection of snowflake ids
     */
    fun nextIdsAsString(size: Int): List<String> {
        return List(size) { nextIdAsString() }
    }

    fun parse(id: Long): SnowflakeId {
        val timestamp = (id shr TIME_STAMP_SHIFT) + EPOCH
        val max = MAX_MACHINE_ID - 1L
        val machineId = (id shr MAX_MACHINE_ID) and max
        val i = id and max

        return SnowflakeId(timestamp, machineId.toInt(), i.toInt())
    }

    fun parse(idString: String): SnowflakeId {
        val id = java.lang.Long.parseLong(idString.toLowerCase(), ALPHA_NUMERIC_BASE)
        return parse(id)
    }

}