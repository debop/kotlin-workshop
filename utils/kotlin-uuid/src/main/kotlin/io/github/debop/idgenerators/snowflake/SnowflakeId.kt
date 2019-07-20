package io.github.debop.idgenerators.snowflake

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
    val value: Long = Snowflake.makeId(timestamp, machineId, increment)
    val valueAsString: String by lazy { value.toString(Snowflake.ALPHA_NUMERIC_BASE) }
}