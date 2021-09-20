package io.github.debop.flashtext

import io.github.debop.ahocorasick.trie.Trie
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

/**
 * KeywordProcessorTest
 * @author debop (Sunghyouk Bae)
 */
class KeywordProcessorTest {

    companion object: KLogging()

    val trie = Trie.builder()
        .addKeywords("NYC")
        .addKeywords("APPL")
        .addKeywords("java_2e", "java programming")
        .addKeywords("PM", "product manager")
        .build()

    val text = "I am a PM for a java_2e platform working from APPL, NYC"

    @Test
    fun `extract keywords`() = runBlocking<Unit> {
        val emits = trie.parseText(text)
        logger.debug { "emits=$emits" }
    }

    @Test
    fun `tokenize keywords`() = runBlocking<Unit> {
        val tokens = trie.tokenize(text)
        logger.debug { "tokens=$tokens" }
    }

    @Test
    fun `replace keywords`() = runBlocking<Unit> {
        val map = mapOf("APPL" to "Apple",
                        "NYC" to "New york",
                        "java_2e" to "java programming",
                        "PM" to "product manager")

        val replaced = trie.replace(text, map)

        replaced shouldBeEqualTo "I am a product manager for a java programming platform working from Apple, New york"
    }
}