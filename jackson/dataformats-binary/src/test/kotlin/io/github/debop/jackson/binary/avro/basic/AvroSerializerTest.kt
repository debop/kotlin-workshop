package io.github.debop.jackson.binary.avro.basic

import io.github.debop.avro.examples.Employee
import io.github.debop.jackson.binary.avro.DefaultAvroSerializer
import io.github.debop.jackson.binary.avro.deserialize
import io.github.debop.jackson.binary.avro.deserializeList
import io.github.debop.kotlin.tests.extensions.Random
import io.github.debop.kotlin.tests.extensions.Randomized
import mu.KLogging
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.RepeatedTest

/**
 * AvroSerializerTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
@Randomized
class AvroSerializerTest: AbstractAvroTest() {

    companion object: KLogging() {
        private const val TEST_COUNT = 10
    }

    val serializer = DefaultAvroSerializer()

    @RepeatedTest(TEST_COUNT)
    fun `serialize single object`(@Random employee: Employee) {
        val bytes = serializer.serialize(employee)
        bytes.shouldNotBeEmpty()

        val converted = serializer.deserialize<Employee>(bytes)
        converted shouldEqual employee
    }

    @RepeatedTest(TEST_COUNT)
    fun `serialize collection of avro object`(@Random(type = Employee::class) emps: List<Employee>) {
        val bytes = serializer.serializeList(emps)
        bytes.shouldNotBeEmpty()

        val converted = serializer.deserializeList<Employee>(bytes)
        converted shouldContainAll emps
    }
}