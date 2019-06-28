package io.github.debop.jackson.dataformat.yaml

import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

/**
 * MultipleRootExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
class MultipleRootExample: AbstractYamlExample() {

    @Test
    fun `parse multiple root`() {
        val yaml = """
            |num: 42
            |---
            |num: -42
            """.trimMargin()

        val iter = yamlMapper
            .readerFor(jacksonTypeRef<Map<String, Int>>())
            .readValues<Map<String, Int>>(yaml)

        val first = iter.nextValue()["num"]
        val second = iter.nextValue()["num"]

        first shouldEqual 42
        second shouldEqual -42

        iter.close()
    }
}