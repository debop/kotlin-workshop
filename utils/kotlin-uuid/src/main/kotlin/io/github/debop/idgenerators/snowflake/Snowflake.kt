package io.github.debop.idgenerators.snowflake

import mu.KLogging
import java.lang.Character.MAX_RADIX
import java.net.NetworkInterface
import java.security.SecureRandom
import kotlin.math.absoluteValue

/**
 * Snowflake
 *
 * @param machineId machine or processor number. number range is [0, 64)
 */
class Snowflake(machineId: Int = createMachineId()) {

    companion object: KLogging() {
        private const val UNUSED_BITS = 1L
        private const val TIMESTAMP_BITS = 41L
        private const val MACHINE_BITS = 10L
        private const val SEQUENCE_BITS = 12L

        // Custom Epoch (UTC = 2015-01-01T00:00:00Z)
        const val EPOCH = 1420070400000L
        const val MAX_MACHINE_ID = 64
        const val ALPHA_NUMERIC_BASE = MAX_RADIX
        const val TIME_STAMP_SHIFT = 22
        const val MACHINE_ID_SHIFT = 16
        const val MAX_SEQUENCE = 4_096

        internal fun makeId(timestamp: Long, machineId: Int, increment: Int) =
            ((timestamp - EPOCH) shl TIME_STAMP_SHIFT) or
            // (machineId shl MACHINE_ID_SHIFT).toLong() or
            increment.toLong()

        internal fun parseId(id: Long): SnowflakeId {
            val timestamp = (id shr TIME_STAMP_SHIFT) + EPOCH
            val max = MAX_MACHINE_ID - 1L
            val machineId = (id shr MACHINE_ID_SHIFT) and max
            val i = id and max

            return SnowflakeId(timestamp, machineId.toInt(), i.toInt())
        }

        fun createMachineId(): Int {
            val machineId = try {
                buildString {
                    NetworkInterface.getNetworkInterfaces().asSequence().forEach { network ->
                        network.hardwareAddress?.let { mac ->
                            // 0242185EE8BE024283EF42AE1CC1DE3256DB
                            mac.forEach { append("%02X".format(it)) }
                        }
                    }
                }.hashCode()
            } catch (ex: Exception) {
                SecureRandom().nextInt()
            }
            logger.debug { "machineId=$machineId" }
            return machineId.absoluteValue % MAX_MACHINE_ID
        }
    }

    /**
     * Machine or process ID
     */
    val machineId = machineId.absoluteValue % MAX_MACHINE_ID
    private val sequencer = Sequencer()

    /**
     * Generate next Snowflake ID
     *
     * @return snowflake id
     */
    fun nextId(): Long {
        val (timestamp, sequence) = sequencer.nextSequence()
        return makeId(timestamp, machineId, sequence)
    }

    /**
     * Generate snowflake ids
     *
     * @param size size of snowflake ids
     * @return collection of snowflake ids
     */
    fun nextIds(size: Int): List<Long> {
        return List(size) {
            val (timestamp, sequence) = sequencer.nextSequence()
            makeId(timestamp, machineId, sequence)
        }
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
        return nextIds(size).map { it.toString(ALPHA_NUMERIC_BASE) }
    }

    fun parse(id: Long): SnowflakeId =
        parseId(id)

    fun parse(idString: String): SnowflakeId {
        val id = java.lang.Long.parseLong(idString.toLowerCase(), ALPHA_NUMERIC_BASE)
        return parseId(id)
    }

    private class Sequencer {
        private var lastTimestamp: Long = -1L
        private var sequence = 0

        fun nextSequence(): Pair<Long, Int> = synchronized(this) {
            var currentTimestamp = System.currentTimeMillis()
            if (currentTimestamp == lastTimestamp) {
                sequence = (sequence + 1) % MAX_SEQUENCE
                if (sequence == 0) {
                    while (currentTimestamp == lastTimestamp) {
                        currentTimestamp = System.currentTimeMillis()
                    }
                    lastTimestamp = currentTimestamp
                }
            } else {
                sequence = 0
                lastTimestamp = currentTimestamp
            }
            return lastTimestamp to sequence
        }
    }
}