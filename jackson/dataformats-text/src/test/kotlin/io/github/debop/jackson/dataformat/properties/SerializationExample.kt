package io.github.debop.jackson.dataformat.properties

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.debop.jackson.dataformat.FiveMinuteUser
import io.github.debop.jackson.dataformat.Gender
import io.github.debop.jackson.dataformat.Point
import io.github.debop.jackson.dataformat.Rectangle
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

/**
 * SerializationExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
@Suppress("UNCHECKED_CAST")
class SerializationExample: AbstractPropertiesExample() {

    @Test
    fun `simple employee serialization`() {
        val input = FiveMinuteUser("Bob", "Palmer", true, Gender.MALE, byteArrayOf(1, 2, 3, 4))
        val output = propsMapper.writeValueAsString(input)

        logger.debug { "output=$output" }

        val expected = """
            |firstName=Bob
            |lastName=Palmer
            |verified=true
            |gender=MALE
            |userImage=AQIDBA==
            |
            """.trimMargin()

        output shouldBeEqualTo expected

        val props = propsMapper.writeValueAsProperties(input)
        props.size shouldBeEqualTo 5
        props["verified"] shouldBeEqualTo "true"
        props["gender"] shouldBeEqualTo "MALE"
    }

    @Test
    fun `deserialize simple POJO`() {
        val input = """
            |firstName=Bob
            |lastName=Palmer
            |verified=true
            |gender=MALE
            |userImage=AQIDBA==
            |
            """.trimMargin()

        val expected = FiveMinuteUser("Bob", "Palmer", true, Gender.MALE, byteArrayOf(1, 2, 3, 4))

        val actual = propsMapper.readValue<FiveMinuteUser>(input)

        actual.shouldNotBeNull()
        actual shouldBeEqualTo expected
    }

    @Test
    fun `serialize rectangle`() {
        val input = Rectangle(Point(1, -2), Point(5, 10))
        val output = propsMapper.writeValueAsString(input)
        logger.debug { "output=$output" }

        val expected = """
            |topLeft.x=1
            |topLeft.y=-2
            |bottomRight.x=5
            |bottomRight.y=10
            |
            """.trimMargin()

        output shouldBeEqualTo expected

        val props = propsMapper.writeValueAsProperties(input)
        props.size shouldBeEqualTo 4
        props["topLeft.x"] shouldBeEqualTo "1"
        props["topLeft.y"] shouldBeEqualTo "-2"
        props["bottomRight.x"] shouldBeEqualTo "5"
        props["bottomRight.y"] shouldBeEqualTo "10"
    }

    @Test
    fun `deserialize rectange`() {
        val input = """
            |topLeft.x=1
            |topLeft.y=-2
            |bottomRight.x=5
            |bottomRight.y=10
            """.trimMargin()
        val expected = Rectangle(Point(1, -2), Point(5, 10))

        val result = propsMapper.readValue<Rectangle>(input)
        result shouldBeEqualTo expected

        val result2 = propsMapper.readValue<Rectangle>(input.toByteArray())
        result2 shouldBeEqualTo expected
    }

    @Test
    fun `deserialize nested map`() {
        val input = "root.comparison.source.database=test\n" +
                    "root.comparison.target.database=test2\n"

        val result = propsMapper.readValue<Map<Any, Any>>(input)
        result.shouldNotBeNull()
        result.size shouldBeEqualTo 1

        logger.debug { "result=$result" }

        val nested = result.getNode("root.comparison")
        nested.size shouldBeEqualTo 2

        logger.debug { "nested=$nested" }

        val source = nested["source"] as Map<Any, Any>
        source["database"] shouldBeEqualTo "test"
    }
}