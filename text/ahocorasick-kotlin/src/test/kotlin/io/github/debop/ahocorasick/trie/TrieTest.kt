package io.github.debop.ahocorasick.trie

import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

/**
 * TrieTest
 * @author debop (Sunghyouk Bae)
 */
class TrieTest {

    companion object: KLogging() {
        val ALPHABET = arrayOf("abc", "bcd", "cde")
        val PRONOUNS = arrayOf("hers", "his", "she", "he")
        val FOOD = arrayOf("veal", "cauliflower", "broccoli", "tomatoes")
        val GREEK_LETTERS = arrayOf("Alpha", "Beta", "Gamma")
        val UNICODE = arrayOf("turning", "once", "again", "börkü")
    }

    @Test
    fun `keyword and text are same`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeyword(ALPHABET[0])
            .build()

        val emits = trie.parseText(ALPHABET[0])
        checkEmit(emits.firstOrNull(), 0, 2, ALPHABET[0])
    }

    @Test
    fun `keyword and text are the same first match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeyword(ALPHABET[0])
            .build()

        val firstMatch = trie.firstMatch(ALPHABET[0])
        checkEmit(firstMatch, 0, 2, ALPHABET[0])
    }

    @Test
    fun `text is longer than keyword`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeyword(ALPHABET[0])
            .build()

        val emits = trie.parseText(" " + ALPHABET[0])
        checkEmit(emits.firstOrNull(), 1, 3, ALPHABET[0])
    }

    @Test
    fun `text is longer than keyword first match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeyword(ALPHABET[0])
            .build()

        val emits = trie.firstMatch(" " + ALPHABET[0])
        checkEmit(emits, 1, 3, ALPHABET[0])
    }

    @Test
    fun `various keywords on match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*ALPHABET)
            .build()

        val emits = trie.parseText("bcd")
        checkEmit(emits.firstOrNull(), 0, 2, "bcd")
    }

    @Test
    fun `various keywords first match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*ALPHABET)
            .build()

        val emits = trie.firstMatch("bcd")
        checkEmit(emits, 0, 2, "bcd")
    }

    @Test
    fun `ushers test and stop on hit`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*PRONOUNS)
            .stopOnHit()
            .build()

        val emits = trie.parseText("ushers")
        emits.size shouldEqualTo 1
        checkEmit(emits.firstOrNull(), 2, 3, "he")
    }

    @Test
    fun `ushers test stop on hit skip first one`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*PRONOUNS)
            .stopOnHit()
            .build()

        val testEmitHandler = object: AbstractStatefulEmitHandler() {
            var first = true
            override fun emit(emit: Emit): Boolean {
                if (first) {
                    first = false
                    return false
                }
                addEmit(emit)
                return true
            }
        }
        trie.parseText("ushers", testEmitHandler)
        val emits = testEmitHandler.emits
        emits.size shouldEqualTo 1
        checkEmit(emits.firstOrNull(), 1, 3, "she")
    }

    @Test
    fun `ushers test`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*PRONOUNS)
            .build()

        val emits = trie.parseText("ushers")
        emits.size shouldEqualTo 3
        checkEmit(emits[0], 2, 3, "he")
        checkEmit(emits[1], 1, 3, "she")
        checkEmit(emits[2], 2, 5, "hers")
    }

    @Test
    fun `ushers test with capital keywords`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .ignoreCase()
            .addKeywords(PRONOUNS.map { it.toUpperCase() })
            .build()

        val emits = trie.parseText("ushers")
        emits.size shouldEqualTo 3
        checkEmit(emits[0], 2, 3, "he")
        checkEmit(emits[1], 1, 3, "she")
        checkEmit(emits[2], 2, 5, "hers")
    }

    @Test
    fun `ushers test first match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*PRONOUNS)
            .build()

        val emits = trie.firstMatch("ushers")
        checkEmit(emits, 2, 3, "he")
    }

    @Test
    fun `ushers test by callback`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*PRONOUNS)
            .build()

        val emits = mutableListOf<Emit>()
        val emitHandler = object: EmitHandler {
            override fun emit(emit: Emit): Boolean = emits.add(emit)
        }
        trie.runParseText("ushers", emitHandler)

        emits.size shouldEqualTo 3
        checkEmit(emits[0], 2, 3, "he")
        checkEmit(emits[1], 1, 3, "she")
        checkEmit(emits[2], 2, 5, "hers")
    }

    @Test
    fun `mis leading test`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeyword("hers")
            .build()

        val emits = trie.parseText("h he her hers")
        checkEmit(emits.firstOrNull(), 9, 12, "hers")
    }

    @Test
    fun `food recipes`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*FOOD)
            .build()

        val emits = trie.parseText("2 cauliflowers, 3 tomatoes, 4 slices of veal, 100g broccoli")
        emits.size shouldEqual 4
        checkEmit(emits[0], 2, 12, "cauliflower")
        checkEmit(emits[1], 18, 25, "tomatoes")
        checkEmit(emits[2], 40, 43, "veal")
        checkEmit(emits[3], 51, 58, "broccoli")
    }

    @Test
    fun `food recipes first match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*FOOD)
            .build()

        val firstMatch = trie.firstMatch("2 cauliflowers, 3 tomatoes, 4 slices of veal, 100g broccoli")
        checkEmit(firstMatch, 2, 12, "cauliflower")
    }

    @Test
    fun `long and short overlapping match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeyword("he")
            .addKeyword("hehehehe")
            .build()

        val emits = trie.parseText("hehehehehe")

        emits.size shouldEqualTo 7
        checkEmit(emits[0], 0, 1, "he")
        checkEmit(emits[1], 2, 3, "he")
        checkEmit(emits[2], 4, 5, "he")
        checkEmit(emits[3], 6, 7, "he")
        checkEmit(emits[4], 0, 7, "hehehehe")
        checkEmit(emits[5], 8, 9, "he")
        checkEmit(emits[6], 2, 9, "hehehehe")
    }

    @Test
    fun `non overlapping`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .ignoreOverlaps()
            .addKeyword("ab")
            .addKeyword("cba")
            .addKeyword("ababc")
            .build()

        val emits = trie.parseText("ababcbab")

        emits.size shouldEqualTo 2
        checkEmit(emits[0], 0, 4, "ababc")
        checkEmit(emits[1], 6, 7, "ab")
    }

    @Test
    fun `non overlapping first match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .ignoreOverlaps()
            .addKeyword("ab")
            .addKeyword("cba")
            .addKeyword("ababc")
            .build()

        val firstMatch = trie.firstMatch("ababcbab")
        checkEmit(firstMatch, 0, 4, "ababc")
    }

    @Test
    fun `contains match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeyword("ab")
            .addKeyword("cba")
            .addKeyword("ababc")
            .build()

        trie.containsMatch("ababcbab").shouldBeTrue()
    }

    @Test
    fun `start of churchill speech`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .ignoreOverlaps()
            .addKeywords("T", "u", "ur", "r", "urn", "ni", "i", "in", "n", "urning")
            .build()

        val emits = trie.parseText("Turning")
        emits.size shouldEqualTo 2
        checkEmit(emits[0], 0, 0, "T")
        checkEmit(emits[1], 1, 6, "urning")
    }

    @Test
    fun `partial match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .onlyWholeWords()
            .addKeyword("sugar")
            .build()

        val emits = trie.parseText("sugarcane sugarcane sugar canesugar") // left, middle, right test
        emits.size shouldEqualTo 1
        checkEmit(emits[0], 20, 24, "sugar")
    }

    @Test
    fun `partial match first match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .onlyWholeWords()
            .addKeyword("sugar")
            .build()

        val firstMatch = trie.firstMatch("sugarcane sugarcane sugar canesugar") // left, middle, right test
        checkEmit(firstMatch, 20, 24, "sugar")
    }

    @Test
    fun `tokenize full sentence`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*GREEK_LETTERS)
            .build()

        val tokens = trie.tokenize("Hear: Alpha team first, Beta from the rear, Gamma in reserve")

        tokens.size shouldEqualTo 7
        tokens[0].fragment shouldEqual "Hear: "
        tokens[1].fragment shouldEqual "Alpha"
        tokens[2].fragment shouldEqual " team first, "
        tokens[3].fragment shouldEqual "Beta"
        tokens[4].fragment shouldEqual " from the rear, "
        tokens[5].fragment shouldEqual "Gamma"
        tokens[6].fragment shouldEqual " in reserve"
    }

    @Test
    fun `string index out of bounds exception`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .ignoreCase()
            .onlyWholeWords()
            .addKeywords(*UNICODE)
            .build()

        val emits = trie.parseText("TurninG OnCe AgAiN BÖRKÜ")

        emits.size shouldEqualTo 4

        checkEmit(emits[0], 0, 6, "turning")
        checkEmit(emits[1], 8, 11, "once")
        checkEmit(emits[2], 13, 17, "again")
        checkEmit(emits[3], 19, 23, "börkü")
    }

    @Test
    fun `test ignorecase`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .ignoreCase()
            .onlyWholeWords()
            .addKeywords(*UNICODE)
            .build()

        val emits = trie.parseText("TurninG OnCe AgAiN BÖRKÜ")

        emits.size shouldEqualTo 4

        checkEmit(emits[0], 0, 6, "turning")
        checkEmit(emits[1], 8, 11, "once")
        checkEmit(emits[2], 13, 17, "again")
        checkEmit(emits[3], 19, 23, "börkü")
    }

    @Test
    fun `test ignorecase first match`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .ignoreCase()
            .onlyWholeWords()
            .addKeywords(*UNICODE)
            .build()

        val firstMatch = trie.firstMatch("TurninG OnCe AgAiN BÖRKÜ")

        checkEmit(firstMatch, 0, 6, "turning")
    }

    @Test
    fun `tokenize Tokens in sequence`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .addKeywords(*GREEK_LETTERS)
            .build()
        val tokens = trie.tokenize("Alpha Beta Gamma")
        logger.debug { "tokens=$tokens" }
        tokens.size shouldEqualTo 5   // 2 space
    }

    @Test
    fun `zero length`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .ignoreCase()
            .ignoreOverlaps()
            .onlyWholeWords()
            .addKeyword("")
            .build()

        val sentence = """
                  |Try a natural lip and subtle bronzer to keep all the focus
                  |on those big bright eyes with NARS Eyeshadow Duo in Rated R And the winner is...
                  |Boots No7 Advanced Renewal Anti-ageing Glycolic Peel Kit (${'$'}25 amazon.com) won
                  |most-appealing peel.
                  """.trimMargin()

        val tokens = trie.tokenize(sentence)

        tokens.size shouldEqualTo 1
        tokens[0].fragment shouldEqual sentence
    }

    @Test
    fun `parse unicode text 1`() = runBlocking<Unit> {
        val target = "LİKE THIS"  // The second character ('İ') is Unicode, which was read by AC as a 2-byte char
        target.substring(5, 9) shouldEqual "THIS"

        val trie = Trie.builder()
            .ignoreCase()
            .onlyWholeWords()
            .addKeyword("this")
            .build()

        val emits = trie.parseText(target)
        emits.size shouldEqualTo 1
        checkEmit(emits[0], 5, 8, "this")
    }

    @Test
    fun `parse unicode text 2`() = runBlocking<Unit> {
        val target = "LİKE THIS"  // The second character ('İ') is Unicode, which was read by AC as a 2-byte char
        target.substring(5, 9) shouldEqual "THIS"

        val trie = Trie.builder()
            .ignoreCase()
            .onlyWholeWords()
            .addKeyword("this")
            .build()

        val firstMatch = trie.firstMatch(target)
        checkEmit(firstMatch, 5, 8, "this")
    }

    @Test
    fun `partial match whitespace`() = runBlocking<Unit> {
        val trie = Trie.builder()
            .onlyWholeWordsWhitespaceSeparated()
            .addKeyword("#sugar-123")
            .build()

        val emits = trie.parseText("#sugar-123 #sugar-1234")
        emits.size shouldEqualTo 1
        checkEmit(emits.firstOrNull(), 0, 9, "#sugar-123")
    }

    @Test
    fun `large string`() = runBlocking<Unit> {
        val interval = 100
        val textSize = 1_000_000
        val keyword = FOOD[2]
        val text = randomNumbers(textSize)

        injectKeyword(text, keyword, interval)

        val trie = Trie.builder()
            .onlyWholeWords()
            .addKeyword(keyword)
            .build()

        val emits = trie.parseText(text)
        emits.size shouldEqualTo textSize / interval
    }

    private fun randomInt(min: Int, max: Int): Int = ThreadLocalRandom.current().nextInt(min, max)

    private fun randomNumbers(count: Int): StringBuilder {
        return StringBuilder(count).apply {
            for (i in 1..count) {
                append(randomInt(0, 10))
            }
        }
    }

    private fun injectKeyword(source: StringBuilder, keyword: String, interval: Int) {
        val length = source.length
        for (i in 0 until length step interval) {
            source.replace(i, i + keyword.length, keyword)
        }
    }

    private fun checkEmit(emit: Emit?, expectedStart: Int, expectedEnd: Int, expectedKeyword: String) {
        logger.trace { "start=$expectedStart, end=$expectedEnd, keyword=$expectedKeyword, emit=$emit" }
        emit.shouldNotBeNull()
        emit.start shouldEqual expectedStart
        emit.end shouldEqual expectedEnd
        emit.keyword shouldEqual expectedKeyword
    }
}