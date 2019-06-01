package io.github.debop.kotlin.workshop.math

import mu.KLogging
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

/**
 * BigDecimalExtensionsTest
 * @author debop (Sunghyouk Bae)
 */
class BigDecimalExtensionsTest {

    companion object : KLogging()

    @Test
    fun `operators of big decimal`() {

        val a = 1.2.toBigDecimal()
        val b = 2.4.toBigDecimal()

        a + a shouldEqualTo b
        b - a shouldEqualTo a

        a * 2 shouldEqualTo b
        2 * a shouldEqualTo b

        a * 2.0 shouldEqualTo b
        2.0 * a shouldEqualTo b
    }
}