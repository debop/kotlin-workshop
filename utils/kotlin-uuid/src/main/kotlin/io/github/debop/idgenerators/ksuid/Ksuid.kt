package io.github.debop.idgenerators.ksuid

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Arrays
import java.util.Date

/**
 * Ksuid
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 19
 */
object Ksuid {

    private const val EPOCH = 1400000000L
    private const val TIMESTAMP_LENGTH = 4
    private const val PAYLOAD_LENGTH = 16
    private const val MAX_ENCODED_LENGTH = 27

    private val base62: Base62 = Base62()
    private val random: SecureRandom = SecureRandom()

    @JvmStatic
    fun generate(): String {
        return generate(generateTimestamp())
    }

    @JvmStatic
    fun generate(date: Date): String {
        return generate(generateTimestamp(date))
    }

    private fun generate(timestamp: ByteArray): String {
        val payload = generatePayload()

        val uid = ByteArrayOutputStream().use { output ->
            runCatching {
                output.write(timestamp)
                output.write(payload)
            }
            base62.encode(output.toByteArray())
        }
        if (uid.length > MAX_ENCODED_LENGTH) {
            return uid.substring(0, MAX_ENCODED_LENGTH)
        }

        return uid
    }

    @JvmStatic
    fun prettyString(ksuid: String): String {
        val bytes = base62.decode(ksuid)
        val timestamp = decodeTimestamp(bytes)
        val utcTimeString = Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.UTC)
        return """
            |Time = $utcTimeString
            |Timestamp = ${timestamp * 1000}
            |Payload = ${decodePayload(bytes)}
            """.trimMargin()
    }

    /**
     * Get the timestamp of the KSUID string.
     *
     * @param ksuid KSUID string
     * @return timestamp in millisecond.
     */
    @JvmStatic
    fun getTimestamp(ksuid: String): Long {
        val bytes = base62.decode(ksuid)
        return decodeTimestamp(bytes) * 1000
    }

    private fun generateTimestamp(): ByteArray {
        val utc = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli() / 1000
        return generateTimestamp(utc)
    }

    private fun generateTimestamp(date: Date): ByteArray {
        val utc = date.toInstant().toEpochMilli() / 1000
        return generateTimestamp(utc)
    }

    private fun generateTimestamp(utcEpockSeconds: Long): ByteArray {
        val timestamp = (utcEpockSeconds - EPOCH).toInt()
        return ByteBuffer.allocate(TIMESTAMP_LENGTH).putInt(timestamp).array()
    }

    fun generatePayload(): ByteArray {
        return ByteArray(PAYLOAD_LENGTH).apply {
            random.nextBytes(this)
        }
    }

    private fun decodeTimestamp(decodedKsuid: ByteArray): Long {
        val timestamp = decodedKsuid.copyOf(TIMESTAMP_LENGTH)
        return ByteBuffer.wrap(timestamp).int.toLong() + EPOCH
    }

    private fun decodePayload(decodedKsuid: ByteArray): String {
        val payload = decodedKsuid.copyOfRange(TIMESTAMP_LENGTH, decodedKsuid.size - TIMESTAMP_LENGTH)
        return Arrays.toString(payload)
    }
}