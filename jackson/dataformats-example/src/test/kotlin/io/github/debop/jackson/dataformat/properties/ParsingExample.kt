package io.github.debop.jackson.dataformat.properties

import com.fasterxml.jackson.module.kotlin.readValue
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
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
        wrapper.map.size shouldEqualTo 3
    }

    @Test
    fun `parse properties`() {
        val props = Properties().apply {
            put("a.b", "14")
            put("x", "foo")
        }
        val result = propsMapper.readPropertiesAs(props, Map::class.java) as Map<String, Any>
        result.shouldNotBeNull()
        result.size shouldEqualTo props.size
        result["x"] shouldEqual props["x"]

        val obj = result["a"]
        obj.shouldNotBeNull()
        obj shouldBeInstanceOf Map::class

        val m2 = obj as Map<Any, Any>
        m2.size shouldEqualTo 1
        m2["b"] shouldEqual "14"
    }


}