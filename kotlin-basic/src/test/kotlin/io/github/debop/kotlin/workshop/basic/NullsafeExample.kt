package io.github.debop.kotlin.workshop.basic

import mu.KLogging
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNullOrEmpty
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * https://kotlinlang.org/docs/reference/null-safety.html
 */
@Suppress("UNUSED_VARIABLE")
class NullsafeExample {

    companion object : KLogging()

    @Test
    fun `nullable type with ?`() {
        var a: String = "abc"
        // a = null // compilation error

        var str: String? = "abc"
        str.shouldNotBeNullOrEmpty()

        str = null
        str.shouldBeNull()
    }

    @Test
    fun `check null in conditions`() {
        var str: String? = "abc"

        val len1 = if (str != null) str.length else -1
        // safe call and elvis operator (?:)
        val len2 = str?.length ?: -1

        len2 shouldEqualTo len1

        str = null
        val len3 = str?.length ?: -1
        len3 shouldEqualTo -1
    }

    @Test
    fun `not null assertion operator`() {
        var str: String? = "abc"

        val len = str!!.length
        len shouldEqualTo 3

        str = null
        assertThrows<NullPointerException> {
            str!!.length
        }
    }

    @Test
    fun `smart cast`() {
        val a: Any? = "abc"

        // smart cast
        val aInt = a as? Int
        aInt.shouldBeNull()

        assertThrows<ClassCastException> {
            // unsafe cast
            a as Int
        }

        assertThrows<ClassCastException> {
            // unsafe cast
            a as Int?
        }
    }

    @Test
    fun `collection methods for nullable type`() {
        val nullableList: List<Int?> = listOf(1, 2, null, 4)
        val ints: List<Int> = nullableList.filterNotNull()
        ints shouldContainAll arrayOf(1, 2, 4)
    }
}