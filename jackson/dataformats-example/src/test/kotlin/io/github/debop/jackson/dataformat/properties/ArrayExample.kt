package io.github.debop.jackson.dataformat.properties

import com.fasterxml.jackson.module.kotlin.readValue
import io.github.debop.jackson.dataformat.Box
import io.github.debop.jackson.dataformat.Container
import io.github.debop.jackson.dataformat.Point
import io.github.debop.jackson.dataformat.Points
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

/**
 * ArrayExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
class ArrayExample: AbstractPropertiesExample() {

    @Test
    fun `serde array of POJO`() {
        val input = Container(listOf(Box(5, 6), Box(-5, 15)))
        val expected = """
            |boxes.1.x=5
            |boxes.1.y=6
            |boxes.2.x=-5
            |boxes.2.y=15
            |
            """.trimMargin()

        val output = propsMapper.writeValueAsString(input)
        logger.debug { "output=$output" }

        output shouldEqual expected

        val parsed = propsMapper.readValue<Container>(output)
        parsed shouldEqual input
    }

    @Test
    fun `serialize points`() {
        val input = Points(Point(1, 2), Point(3, 4), Point(5, 6))
        val expected = """
            |p.1.x=1
            |p.1.y=2
            |p.2.x=3
            |p.2.y=4
            |p.3.x=5
            |p.3.y=6
            |
            """.trimMargin()

        val output = propsMapper.writeValueAsString(input)
        logger.debug { "output=$output" }

        output shouldEqual expected

        val parsed = propsMapper.readValue<Points>(output)
        parsed shouldEqual input
    }
}