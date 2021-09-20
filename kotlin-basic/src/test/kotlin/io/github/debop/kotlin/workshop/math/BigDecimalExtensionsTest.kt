package io.github.debop.kotlin.workshop.math

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
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

        a + a shouldBeEqualTo b
        b - a shouldBeEqualTo a

        a * 2 shouldBeEqualTo b
        2 * a shouldBeEqualTo b

        a * 2.0 shouldBeEqualTo b
        2.0 * a shouldBeEqualTo b
    }
}