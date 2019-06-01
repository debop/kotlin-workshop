package io.github.debop.kotlin.workshop.utils

import mu.KLogging
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * NumberExtensionsTest
 * @author debop (Sunghyouk Bae)
 */
class NumberExtensionsTest {

    companion object : KLogging()

    @ParameterizedTest
    @ValueSource(ints = [Int.MIN_VALUE, -1024, -1, 0, 1, 128, 256, 1024, Int.MAX_VALUE])
    fun `convert Int with ByteArray`(number: Int) {
        number.toByteArray().toInt() shouldEqualTo number
    }

    @ParameterizedTest
    @ValueSource(longs = [Long.MIN_VALUE, -1024, -1, 0, 1, 128, 256, 1024, Long.MAX_VALUE])
    fun `convert Long with ByteArray`(number: Long) {
        number.toByteArray().toLong() shouldEqualTo number
    }
}