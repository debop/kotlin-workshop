package io.github.debop.idgenerators.utils

/**
 * BitInputStream
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 19
 */
class BitInputStream(private val bytes: ByteArray) {

    private val bitLength: Int = bytes.size * 8
    private var offset: Int = 0

    val hasMore: Boolean get() = offset < bitLength

    fun seekBit(pos: Int) {
        offset += pos
        if (offset < 0 || offset > bitLength) {
            throw IndexOutOfBoundsException("Invalid offset. offset=$offset")
        }
    }

    fun readBits(bitsCount: Int): Int {
        require(bitsCount in 0..7) { "bitsCounts should in range 0..7 but bitsCount=$bitsCount" }

        val bitNum = offset % 8
        val byteNum = offset / 8

        val firstRead = minOf(8 - bitNum, bitsCount)
        val secondRead = bitsCount - firstRead

        var result = (bytes[byteNum].toInt() and ((1 shl firstRead) - 1 shl bitNum)).ushr(bitNum)
        if (secondRead > 0 && byteNum + 1 < bytes.size) {
            result = result or (bytes[byteNum + 1].toInt() and ((1 shl secondRead) - 1) shl firstRead)
        }
        offset += bitsCount
        return result
    }
}