package io.github.debop.kotlin.tests.mockk.examples.baeldung

import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

/**
 * HierarchicalMockingExample
 *
 * @author debop
 * @since 19. 6. 13
 */
class HierarchicalMockingExample {

    class Foo {
        lateinit var name: String
        lateinit var bar: Bar
    }

    class Bar {
        lateinit var nickname: String
    }

    @Test
    fun `hierarchical mocking`() {
        // given
        val foo = mockk<Foo> {
            every { name } returns "Karol"
            every { bar } returns mockk {
                every { nickname } returns "Tomato"
            }
        }

        foo.name shouldEqual "Karol"
        foo.bar.nickname shouldEqual "Tomato"
    }
}