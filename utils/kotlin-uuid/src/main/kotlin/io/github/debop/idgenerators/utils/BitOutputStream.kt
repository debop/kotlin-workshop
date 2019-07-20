package io.github.debop.idgenerators.utils

/**
 * BitOutputStream
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 19
 */
class BitOutputStream(capacity: Int) {

    private val bytes: ByteArray = ByteArray(capacity / 8)
    private var offset: Int = 0

    val bitsCountUpToByte: Int
        get() = when (val currentBit = offset % 8) {
            0    -> 0
            else -> 8 - currentBit
        }

    fun toArray(): ByteArray =
        when (val newLength = offset / 8) {
            bytes.size -> bytes
            else       -> bytes.copyOf(newLength)
        }

    fun writeBits(bitsCount: Int, bits: Int) {
        val bitNum = offset % 8
        val byteNum = offset / 8

        val firstWrite = minOf(8 - bitNum, bitsCount)
        val secondWrite = bitsCount - firstWrite

        bytes[byteNum] = (bytes[byteNum].toInt() or (bits and (1 shl firstWrite) - 1 shl bitNum)).toByte()
        if (secondWrite > 0) {
            bytes[byteNum + 1] = (bytes[byteNum + 1].toInt() or (bits.ushr(firstWrite) and (1 shl secondWrite) - 1)).toByte()
        }
        offset += bitsCount
    }
}