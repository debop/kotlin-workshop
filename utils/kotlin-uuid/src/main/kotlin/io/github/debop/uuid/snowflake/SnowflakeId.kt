package io.github.debop.uuid.snowflake

import java.io.Serializable

/**
 * SnowflakeId
 *
 * @param timestamp 생성일자
 * @param machineId machine or processor id
 * @param increment sequence number
 */
data class SnowflakeId(val timestamp: Long,
                       val machineId: Int,
                       val increment: Int): Serializable {
    /**
     * Snowflake의 Long 값
     */
    val longValue: Long = Snowflake.makeId(timestamp, machineId, increment)
    val asString: String by lazy { longValue.toString(Snowflake.ALPHA_NUMERIC_BASE) }
}