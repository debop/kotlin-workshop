package io.github.debop.jackson.dataformat.properties

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.debop.jackson.dataformat.FiveMinuteUser
import io.github.debop.jackson.dataformat.Gender
import io.github.debop.jackson.dataformat.Point
import io.github.debop.jackson.dataformat.Rectangle
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
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

        output shouldEqual expected

        val props = propsMapper.writeValueAsProperties(input)
        props.size shouldEqualTo 5
        props["verified"] shouldEqual "true"
        props["gender"] shouldEqual "MALE"
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
        actual shouldEqual expected
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

        output shouldEqual expected

        val props = propsMapper.writeValueAsProperties(input)
        props.size shouldEqualTo 4
        props["topLeft.x"] shouldEqual "1"
        props["topLeft.y"] shouldEqual "-2"
        props["bottomRight.x"] shouldEqual "5"
        props["bottomRight.y"] shouldEqual "10"
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
        result shouldEqual expected

        val result2 = propsMapper.readValue<Rectangle>(input.toByteArray())
        result2 shouldEqual expected
    }

    @Test
    fun `deserialize nested map`() {
        val input = "root.comparison.source.database=test\n" +
                    "root.comparison.target.database=test2\n"

        val result = propsMapper.readValue<Map<Any, Any>>(input)
        result.shouldNotBeNull()
        result.size shouldEqualTo 1

        logger.debug { "result=$result" }

        val nested = result.getNode("root.comparison")
        nested.size shouldEqualTo 2

        logger.debug { "nested=$nested" }

        val source = nested["source"] as Map<Any, Any>
        source["database"] shouldEqual "test"
    }
}