package io.github.debop.kotlin.workshop.basic

import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

/**
 * https://kotlinlang.org/docs/reference/multi-declarations.html
 */
class DestructuringExample {

    companion object : KLogging()

    @Test
    fun `destruct variables`() {
        val (a, b) = Pair(1, "a")
        a shouldEqualTo 1
        b shouldEqual "a"

        val (first, second) = "Hello world".split(" ", limit = 2)
        first shouldEqual "Hello"
        second shouldEqual "world"
    }


    data class User(val id: Long, val name: String)

    @Test
    fun `destruct from data class`() {
        val user = User(1, "debop")

        val (id, name) = user

        id shouldEqual user.id
        name shouldEqual user.name
    }

    @Test
    fun `destruct from map`() {
        val map = mapOf(1 to "debop", 2 to "diego")
        for ((id, name) in map) {
            println("id=$id, name=$name")
        }
    }

    @Test
    fun `destructuring in lambda`() {
        val map = mapOf(1 to "debop", 2 to "diego")

        map.forEach { (id, name) ->
            println("id=$id, name=$name")
        }

        map.entries.forEach { entry ->
            println("key=${entry.key}, value=${entry.value}")
        }
    }
}