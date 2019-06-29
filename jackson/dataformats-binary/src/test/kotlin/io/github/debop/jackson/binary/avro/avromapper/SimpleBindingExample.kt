package io.github.debop.jackson.binary.avro.avromapper

import com.fasterxml.jackson.dataformat.avro.AvroMapper
import com.fasterxml.jackson.dataformat.avro.AvroSchema
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test

/**
 * SimpleBindingExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 28
 */
class SimpleBindingExample {

    companion object: KLogging()

    val mapper: AvroMapper by lazy { AvroMapper().apply { registerKotlinModule() } }

    private data class Employee(val name: String,
                                val age: Int,
                                val emails: List<String>) {
        var boss: Employee? = null
    }

    @Test
    fun `binding simple type with avro schema`() {
        val employee = Employee("Debop", 51, listOf("debop@coupang.com"))

        val employeeSchema: AvroSchema = mapper.schemaFor(Employee::class.java)
        logger.trace { "Schema=${employeeSchema.avroSchema.toString(true)}" }

        val avro = mapper.writer(employeeSchema).writeValueAsBytes(employee)

        val actual: Employee = mapper.readerFor(Employee::class.java).with(employeeSchema).readValue(avro)
        actual shouldEqual employee
    }

    @Test
    fun `binding simple type with schema generator`() {
        val employee = Employee("Debop", 51, listOf("debop@coupang.com"))

        val schemaGenerator = AvroSchemaGenerator()
        mapper.acceptJsonFormatVisitor(Employee::class.java, schemaGenerator)
        val schema = schemaGenerator.generatedSchema
        logger.trace { "Schema=${schema.avroSchema.toString(true)}" }

        val avro = mapper.writer(schema).writeValueAsBytes(employee)
        avro.shouldNotBeEmpty()

        val actual: Employee = mapper.readerFor(Employee::class.java).with(schema).readValue(avro)
        actual shouldEqual employee
    }
}