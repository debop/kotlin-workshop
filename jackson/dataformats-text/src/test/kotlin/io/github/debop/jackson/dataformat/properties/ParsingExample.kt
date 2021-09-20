package io.github.debop.jackson.dataformat.properties

import com.fasterxml.jackson.module.kotlin.readValue
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import java.util.Properties

/**
 * ParsingExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
@Suppress("UNCHECKED_CAST")
class ParsingExample: AbstractPropertiesExample() {

    class MapWrapper {
        var map: MutableMap<String, String> = mutableMapOf()
    }

    @Test
    fun `map with branch`() {
        val props = """
            |map=first
            |map.b = second
            |map.xyz = third
        """.trimMargin()

        val wrapper = propsMapper.readValue<MapWrapper>(props)

        wrapper.map.shouldNotBeNull()
        wrapper.map.size shouldBeEqualTo 3
    }

    @Test
    fun `parse properties`() {
        val props = Properties().apply {
            put("a.b", "14")
            put("x", "foo")
        }
        val result = propsMapper.readPropertiesAs(props, Map::class.java) as Map<String, Any>
        result.shouldNotBeNull()
        result.size shouldBeEqualTo props.size
        result["x"] shouldBeEqualTo props["x"]

        val obj = result["a"]
        obj.shouldNotBeNull()
        obj shouldBeInstanceOf Map::class

        val m2 = obj as Map<Any, Any>
        m2.size shouldBeEqualTo 1
        m2["b"] shouldBeEqualTo "14"
    }


}