package io.github.debop.jackson.dataformat.yaml

import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import java.io.StringWriter

/**
 * YamlGenerationExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
class YamlGenerationExample: AbstractYamlExample() {


    @Test
    fun `generate POJO`() {
        val writer = StringWriter()
        val generator = yamlFactory.createGenerator(writer)

        generator.writeBradDoc()
        generator.close()

        val yaml = writer.toString().trimYamlDocMarker()
        logger.debug { "generated yaml=$yaml" }
        val expected = """
            |name: "Brad"
            |age: 39
            """.trimMargin()
        yaml shouldEqual expected

        writer.close()
    }

    private fun YAMLGenerator.writeBradDoc() {
        writeStartObject()
        writeStringField("name", "Brad")
        writeNumberField("age", 39)
        writeEndObject()
    }
}