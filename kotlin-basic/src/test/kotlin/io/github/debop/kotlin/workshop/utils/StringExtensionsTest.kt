package io.github.debop.kotlin.workshop.utils

import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

class StringExtensionsTest {

    companion object : KLogging()

    @Test
    fun `string is null or empty`() {
        null.ifNullOrEmpty { "<null>" } shouldEqual "<null>"
        "".ifNullOrEmpty { "<empty>" } shouldEqual "<empty>"
        "  ".ifNullOrEmpty { "<empty>" } shouldEqual "  "
    }

    @Test
    fun `string is null or whitespace`() {
        null.ifNullOrBlank { "<null>" } shouldEqual "<null>"
        "".ifNullOrBlank { "<empty>" } shouldEqual "<empty>"
        "  ".ifNullOrBlank { "<empty>" } shouldEqual "<empty>"
        " \t  ".ifNullOrBlank { "<empty>" } shouldEqual "<empty>"
    }
}