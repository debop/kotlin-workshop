package io.github.debop.idgenerators.ksuid

import io.github.debop.idgenerators.utils.BitInputStream
import io.github.debop.idgenerators.utils.BitOutputStream
import mu.KLogging

/**
 * Base62
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 19
 */
class Base62 {

    /**
     * Encodes binary data using a Base62 algorithm.
     * @param data binary data to encode
     * @return String containing Base62 characters
     */
    fun encode(data: ByteArray): String {
        val input = BitInputStream(data)

        // Reserving capacity for the worst case when each output character represents compacted 5-bits data
        return buildString(data.size * 8 / 5 + 1) {
            while (input.hasMore) {
                // Read not greater than 6 bits from the stream
                val rawBits = input.readBits(6)

                // For some cases special processing is needed,
                // so _bits_ will contain final data representation needed to form next output character
                val bits: Int
                if (rawBits and COMPACK_MASK == COMPACK_MASK) {
                    // We can't represent all 6 bits of the data, so extract only least significant 5 bits and return for
                    // one bit back in the stream
                    bits = rawBits and MASK_5BITS
                    input.seekBit(-1)
                } else {
                    bits = rawBits
                }
                append(ENCODE_TABLE[bits])
            }
        }
    }

    /**
     * Decodes a Base62 String into byte array.
     * @param base62String String containing Base62 data
     * @return Array containing decoded data.
     */
    fun decode(base62String: String): ByteArray {
        if (base62String.isEmpty()) {
            return ByteArray(0)
        }

        val length = base62String.length
        val output = BitOutputStream(length * 6)

        var lastCharPos: Int = length - 1
        repeat(length) { i ->
            // Obtain data bits from decoding table for the next character
            val bits = decodedBitsForCharacter(base62String[i])

            // Determine bits count needed to write to the stream
            val bitsCount: Int = when {
                (bits and COMPACK_MASK) == COMPACK_MASK -> 5
                i >= lastCharPos                        -> output.bitsCountUpToByte
                else                                    -> 6
            }
            output.writeBits(bitsCount, bits)
        }

        return output.toArray()
    }

    companion object: KLogging() {
        /**
         * This array is a lookup table that translates 6-bit positive integer index values into their "Base62 Alphabet"
         * equivalents as specified in Table 1 of RFC 2045 excepting special characters for 62 and 63 values.
         *
         * Thanks to "commons" project in ws.apache.org for this code.
         * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
         */
        private val ENCODE_TABLE = charArrayOf(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        )

        /**
         * This array is a lookup table that translates Unicode characters drawn from the "Base64 Alphabet" (as specified in
         * Table 1 of RFC 2045) into their 6-bit positive integer equivalents. Characters that are not in the Base62
         * alphabet but fall within the bounds of the array are translated to -1.
         *
         * Note that there is no special characters in Base62 alphabet that could represent 62 and 63 values, so they both
         * is absent in this decode table.
         *
         * Thanks to "commons" project in ws.apache.org for this code.
         * http://svn.apache.org/repos/asf/webservices/commons/trunk/modules/util/
         */
        private val DECODE_TABLE = byteArrayOf(
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
            18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27,
            28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43,
            44, 45, 46, 47, 48, 49, 50, 51
        )

        /**
         * Special mask for the data that should be written in compact 5-bits form
         */
        private const val COMPACK_MASK = 0x1E // 00011110

        /**
         * Mask for extracting 5 bits of the data
         */
        private const val MASK_5BITS = 0x1F // 0001111

        private fun decodedBitsForCharacter(char: Char): Int {
            val result: Int = DECODE_TABLE[char.code].toInt()
            if (char.code >= DECODE_TABLE.size || result < 0) {
                throw IllegalArgumentException("Wrong Base62 symbol found: $char")
            }
            return result
        }
    }
}
