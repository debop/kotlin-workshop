package io.github.debop.springdata.jpa.mapping.inheritance.joined

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull


class JoinedInheritanceTest: AbstractDataJpaTest() {

    @Autowired
    private lateinit var empRepo: JoinedEmployeeRepository

    @Autowired
    private lateinit var customerRepo: JoinedCustomerRepository

    @Test
    fun `inheritance with joined table`() {

        val emp1 = Employee(name = "Debop", ssn = "111111-1111111", empNo = "004444")
        val emp2 = Employee(name = "Kally", ssn = "222222-2222222", empNo = "009999")

        emp1.members.add(emp2)
        emp2.manager = emp1

        val customer = Customer(name = "Black", ssn = "33333-333333", mobile = "010-5555-5555")
        customer.contactEmployee = emp2

        empRepo.save(emp1)
        customerRepo.save(customer)
        flushAndClear()

        val customer1 = customerRepo.findByIdOrNull(customer.id)!!
        customer1 shouldBeEqualTo customer
        customer1.contactEmployee shouldBeEqualTo emp2

        val employee1 = empRepo.findByIdOrNull(emp1.id)!!
        employee1 shouldBeEqualTo emp1
        employee1.members shouldContainAll setOf(emp2)

        employee1.members.forEach { it.manager = null }
        employee1.members.clear()
        empRepo.delete(employee1)
        customerRepo.delete(customer1)

        flushAndClear()

        empRepo.findAll().shouldNotBeEmpty()  // emp2
        customerRepo.findAll().shouldBeEmpty()
    }
}
