package io.github.debop.kotlin.tests.mockk.examples

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

/**
 * BasicExample
 *
 * @author debop
 * @since 19. 6. 13
 */
class BasicExample {

    interface Generator {
        fun generate(): String
    }

    class Dao {
        fun insert(record: String) = println("Inserting '$record'")
    }

    class Service(private val generator: Generator, private val dao: Dao) {
        fun calculate() {
            val record = generator.generate()
            dao.insert(record)
        }
    }

    val generator = mockk<Generator>()
    val dao = mockk<Dao>()
    val service = Service(generator, dao)

    @Test
    fun `simple mock example with class`() {

        val mockedRecord = "mocked String"
        every { generator.generate() } returns mockedRecord
        every { dao.insert(mockedRecord) } just Runs

        service.calculate()

        verify {
            generator.generate()
            dao.insert(mockedRecord)
        }

    }
}