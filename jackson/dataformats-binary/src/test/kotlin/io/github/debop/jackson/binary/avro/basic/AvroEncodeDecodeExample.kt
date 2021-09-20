package io.github.debop.jackson.binary.avro.basic

import io.github.debop.avro.examples.Employee
import io.github.debop.avro.examples.ProductRoot
import io.github.debop.kotlin.tests.extensions.Random
import io.github.debop.kotlin.tests.extensions.Randomized
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

/**
 * AvroEncodeDecodeExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
@Randomized
class AvroEncodeDecodeExample: AbstractAvroTest() {

    companion object: KLogging() {
        private const val COUNT = 10
    }

    @RepeatedTest(COUNT)
    fun `convert single entity to ByteBuffer`(@Random employee: Employee) {
        val buffer = employee.toByteBuffer()

        val converted = Employee.fromByteBuffer(buffer)
        converted shouldBeEqualTo employee
    }

    @RepeatedTest(COUNT)
    fun `decode single entity`(@Random employee: Employee) {
        val buffer = employee.toByteBuffer()

        val decoded = Employee.getDecoder().decode(buffer)
        decoded shouldBeEqualTo employee
    }

    @Test
    fun `convert nested entity with enum property to ByteBuffer`() {
        val productRoot = createProductRoot()

        val buffer = productRoot.toByteBuffer()

        val converted = ProductRoot.fromByteBuffer(buffer)
        converted shouldBeEqualTo productRoot
    }

    @Test
    fun `decode nested entity with enum property`() {
        val productRoot = createProductRoot()

        val buffer = productRoot.toByteBuffer()

        val decoded = ProductRoot.getDecoder().decode(buffer.array())
        decoded shouldBeEqualTo productRoot
    }
}