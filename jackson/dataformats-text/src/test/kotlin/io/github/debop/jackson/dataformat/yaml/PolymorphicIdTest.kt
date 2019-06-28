package io.github.debop.jackson.dataformat.yaml

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.module.kotlin.readValue
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test


/**
 * PolymorphicIdTest
 *
 * @author debop
 * @since 19. 6. 28
 */
class PolymorphicIdTest: AbstractYamlExample() {

    class Wrapper {
        var nested: Nested? = null
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes(JsonSubTypes.Type(value = SingleNested::class),
                  JsonSubTypes.Type(value = MultipleNested::class))
    interface Nested

    @JsonTypeName("single")
    class SingleNested: Nested {
        var value: String? = null
    }

    @JsonTypeName("multiple")
    class MultipleNested: Nested {
        var value: String? = null
        val props: MutableSet<String> = mutableSetOf()
    }

    @Test
    fun `parse polymorphic with type and value`() {
        val yaml = """
            |nested:
            |  type: single
            |  value: whatever
            """.trimMargin()

        val top = yamlMapper.readValue<Wrapper>(yaml)
        top.shouldNotBeNull()
        top.nested.shouldNotBeNull() shouldBeInstanceOf SingleNested::class
        (top.nested as SingleNested).value shouldEqual "whatever"
    }

    @Test
    fun `parse polymorphic with type without value`() {
        val yaml = """
            |nested:
            |  type: single
            """.trimMargin()

        val top = yamlMapper.readValue<Wrapper>(yaml)
        top.shouldNotBeNull()
        top.nested.shouldNotBeNull() shouldBeInstanceOf SingleNested::class
        (top.nested as SingleNested).value.shouldBeNull()
    }

    @Test
    fun `parse polymorphic with multiple type and value`() {
        val yaml = """
            |nested:
            |  type: multiple
            |  value: whatever
            |  props:
            |    - option1
            |    - option2
            """.trimMargin()

        val top = yamlMapper.readValue<Wrapper>(yaml)
        top.shouldNotBeNull()
        top.nested.shouldNotBeNull() shouldBeInstanceOf MultipleNested::class

        val nested = top.nested as MultipleNested
        nested.value shouldEqual "whatever"
        nested.props shouldContainAll listOf("option1", "option2")
    }

    @Test
    fun `parse polymorphic define type directly`() {
        val yaml = """
            |nested: !single
            |  value: foobar
            """.trimMargin()

        val top = yamlMapper.readValue<Wrapper>(yaml)
        top.shouldNotBeNull()
        top.nested.shouldNotBeNull() shouldBeInstanceOf SingleNested::class
        (top.nested as SingleNested).value shouldEqual "foobar"

    }

    @Test
    fun `parse polymorphic define type directly without value`() {
        val yaml = """
            |nested: !single {}
            """.trimMargin()

        val top = yamlMapper.readValue<Wrapper>(yaml)
        top.shouldNotBeNull()
        top.nested.shouldNotBeNull() shouldBeInstanceOf SingleNested::class
        (top.nested as SingleNested).value.shouldBeNull()
    }
}