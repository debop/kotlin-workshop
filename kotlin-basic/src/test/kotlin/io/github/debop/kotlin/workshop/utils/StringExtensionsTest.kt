package io.github.debop.kotlin.workshop.utils

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class StringExtensionsTest {

    companion object : KLogging()

    @Test
    fun `string is null or empty`() {
        null.ifNullOrEmpty { "<null>" } shouldBeEqualTo "<null>"
        "".ifNullOrEmpty { "<empty>" } shouldBeEqualTo "<empty>"
        "  ".ifNullOrEmpty { "<empty>" } shouldBeEqualTo "  "
    }

    @Test
    fun `string is null or whitespace`() {
        null.ifNullOrBlank { "<null>" } shouldBeEqualTo "<null>"
        "".ifNullOrBlank { "<empty>" } shouldBeEqualTo "<empty>"
        "  ".ifNullOrBlank { "<empty>" } shouldBeEqualTo "<empty>"
        " \t  ".ifNullOrBlank { "<empty>" } shouldBeEqualTo "<empty>"
    }
}