package io.github.debop.jackson.binary.avro

import com.fasterxml.jackson.dataformat.avro.AvroMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

/**
 * AvroJacksonSerializerTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
class AvroJacksonSerializerTest {

    companion object: KLogging()

    val mapper = AvroMapper().apply { registerKotlinModule() }
    val serializer = AvroJacksonSerializer(mapper)

    private data class Employee(val name: String,
                                val age: Int,
                                val emails: List<String>) {
        var boss: Employee? = null
    }

    @Test
    fun `serialize POJO`() {
        val employee = Employee("Debop", 51, listOf("debop@coupang.com"))

        val avroBytes = serializer.serialize(employee)

        val copied = serializer.deserialize(avroBytes, Employee::class.java)
        copied shouldEqual employee
    }
}