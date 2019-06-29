package io.github.debop.jackson.binary.avro.basic

import io.github.debop.avro.examples.Employee
import io.github.debop.avro.examples.EmployeeList
import io.github.debop.avro.examples.ProductProperty
import io.github.debop.avro.examples.ProductRoot
import io.github.debop.avro.examples.Suit
import mu.KLogging

/**
 * AbstractAvroTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
abstract class AbstractAvroTest {

    companion object: KLogging() {
        const val COUNT = 1000
    }

    protected fun createEmployee(id: Int): Employee {
        return Employee.newBuilder()
            .setId(id)
            .setName("name-$id")
            .setAddress("Seoul")
            .setSalary(1000L)
            .setAge(51)
            .setHireAt(System.currentTimeMillis())
            .build()
    }

    protected fun createEmployeeList(count: Int = COUNT): EmployeeList {
        return EmployeeList.newBuilder()
            .setEmps(List(count) { createEmployee(it) })
            .build()
    }

    protected fun createProductProperty(id: Long = 1L): ProductProperty {
        val values = mapOf("name" to "Sunghyouk", "nick" to "debop")

        return ProductProperty.newBuilder()
            .setId(id)
            .setKey("$id")
            .setCreatedAt(System.currentTimeMillis())
            .setModifiedAt(System.currentTimeMillis())
            .setValid(true)
            .setValues(values)
            .build()
    }

    protected fun createProductRoot(): ProductRoot {
        return ProductRoot.newBuilder()
            .setId(12)
            .setCategoryId(30L)
            .setProductProperties(listOf(createProductProperty(1L), createProductProperty(2L)))
            .setSuit(Suit.HEARTS)
            .build()
    }
}