package io.github.debop.jackson.binary.avro.avromapper

import com.fasterxml.jackson.dataformat.avro.AvroMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.debop.avro.examples.Employee
import io.github.debop.jackson.binary.avro.JacksonAvronSerializer
import io.github.debop.jackson.binary.avro.deserialize
import io.github.debop.jackson.binary.avro.deserializeList
import io.github.debop.kotlin.tests.extensions.Random
import io.github.debop.kotlin.tests.extensions.Randomized
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.junit.jupiter.api.RepeatedTest

@Randomized
class JacksonAvronSerializerTest {

    companion object: KLogging() {
        private const val TEST_COUNT = 10
    }

    val mapper = AvroMapper().apply { registerKotlinModule() }
    val serializer = JacksonAvronSerializer(mapper)


    @RepeatedTest(TEST_COUNT)
    fun `serialize avro object`(@Random employee: Employee) {
        val avroBytes = serializer.serialize(employee)

        val copied = serializer.deserialize<Employee>(avroBytes)
        copied shouldBeEqualTo employee
    }

    @RepeatedTest(TEST_COUNT)
    fun `serialize collection of avro object`(@Random(type = Employee::class) emps: List<Employee>) {
        val bytes = serializer.serializeList(emps)

        val converted = serializer.deserializeList<Employee>(bytes)
        converted shouldContainAll emps
    }
}