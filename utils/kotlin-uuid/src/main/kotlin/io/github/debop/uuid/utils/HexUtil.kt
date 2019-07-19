package io.github.debop.uuid.utils

/**
 * HexUtil
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 20
 */
object HexUtil {

    private val ZERO_PADDING_64 = "0".repeat(64)
    private val ZERO_PADDING_16 = "0".repeat(16)

    fun bin(n: Long): String =
        java.lang.Long.toBinaryString(n).leftPad64()

    fun hex(n: Long): String =
        java.lang.Long.toHexString(n).toUpperCase().leftPad16()


    private fun diode(offset: Int, length: Int): Long {
        if (offset < 0 || length < 0 || (offset + length) > 64) {
            throw IllegalArgumentException("bits ranges: [0, 64), offset=$offset, length=$length")
        }
        if (length == 0) return 0L
        if (length == 64) return -1L

        val lb = 64 - offset
        val rb = 64 - (offset + length)

        return (-1L shl lb) or (-1L shl rb)
    }

    private fun String.leftPad64(): String = ZERO_PADDING_64.substring(length) + this
    private fun String.leftPad16(): String = ZERO_PADDING_16.substring(length) + this

}