package io.github.debop.jackson.binary.avro.basic

import io.github.debop.avro.examples.Employee
import io.github.debop.avro.examples.EmployeeList
import io.github.debop.avro.examples.ProductRoot
import io.github.debop.jackson.binary.avro.DefaultAvroGenericRecordSerializer
import io.github.debop.kotlin.tests.extensions.Random
import io.github.debop.kotlin.tests.extensions.Randomized
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * AvroGenericRecordSerializerTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
@Randomized
class AvroGenericRecordSerializerTest: AbstractAvroTest() {

    companion object: KLogging()

    val genericRecordSerializer = DefaultAvroGenericRecordSerializer()

    @Test
    fun `serialize entity`(@Random employee: Employee) {
        val schema = Employee.getClassSchema()

        val bytes = genericRecordSerializer.serialize(employee, schema)
        bytes.shouldNotBeEmpty()

        val converted = genericRecordSerializer.deserialize(bytes, schema)
        converted.shouldNotBeNull()
        logger.trace { "converted=$converted" }
        converted.toString() shouldEqual employee.toString()
    }

    @Test
    fun `serialize collections`(@Random(type = Employee::class) emps: List<Employee>) {
        val empList = EmployeeList.newBuilder().setEmps(emps).build()
        val schema = EmployeeList.getClassSchema()

        val bytes = genericRecordSerializer.serialize(empList, schema)
        bytes.shouldNotBeEmpty()

        val converted = genericRecordSerializer.deserialize(bytes, schema)
        converted.shouldNotBeNull()
        logger.trace { "converted=$converted" }
        // generic record 는 이렇게 비교할 수 밖에 없다 (수형이 없고, map 형식이므로)
        converted.toString() shouldEqual empList.toString()
    }

    @Disabled("enum 수형을 GenericRecord 으로 변환하는 할 때 제대로 못한다!!!")
    @Test
    fun `serialize nested entity`() {
        val product = createProductRoot()
        val schema = ProductRoot.getClassSchema()

        val bytes = genericRecordSerializer.serialize(product, schema)
        bytes.shouldNotBeEmpty()

        val converted = genericRecordSerializer.deserialize(bytes, schema)
        converted.shouldNotBeNull()
        logger.trace { "converted=$converted" }
    }
}