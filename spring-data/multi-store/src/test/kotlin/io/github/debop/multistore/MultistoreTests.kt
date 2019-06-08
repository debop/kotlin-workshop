package io.github.debop.multistore

import io.github.debop.multistore.customer.Customer
import io.github.debop.multistore.customer.CustomerRepository
import io.github.debop.multistore.order.Order
import io.github.debop.multistore.order.OrderItem
import io.github.debop.multistore.order.OrderRepository
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * MultistoreTests
 * @author debop (Sunghyouk Bae)
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
class MultistoreTests : AbstractMultistoreTests() {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var customerRepo: CustomerRepository

    @Autowired
    private lateinit var orderRepo: OrderRepository

    @Test
    fun `repositories is injected`() {
        customerRepo.shouldNotBeNull()
        orderRepo.shouldNotBeNull()
    }

    @Transactional
    @Test
    fun `operation jpa entity`() {
        val customer = createCustomer("debop")
        customerRepo.saveAndFlush(customer)

        customer.id.shouldNotBeNull()

        entityManager.clear()

        val loaded = customerRepo.findByIdOrNull(customer.id)!!
        loaded shouldEqual customer

    }

    @Test
    fun `operation mongodb entity`() {
        val order = createOrder("1004")
        orderRepo.save(order)

        val loaded = orderRepo.findByIdOrNull(order.id)!!
        loaded shouldEqual order

        orderRepo.save(createOrder("1004"))
        orderRepo.save(createOrder("1004"))

        val orders = orderRepo.findByCustomerId("1004")
        orders.size shouldEqual 3
    }

    @Transactional
    @Test
    fun `save to multistore and load entities`() {
        val customer = createCustomer("debop")
        customerRepo.saveAndFlush(customer)
        val customerId = customer.id!!.toString()

        val orders = listOf(createOrder(customerId), createOrder(customerId), createOrder(customerId))
        orderRepo.saveAll(orders)

        val customer2 = customerRepo.findByIdOrNull(customer.id)!!

        val orders2 = orderRepo.findByCustomerId(customer2.id!!.toString())
        orders2 shouldContainAll orders
    }

    private fun createCustomer(name: String): Customer {
        return Customer(name = name).apply {
            address.street = "jamsil"
            address.zipcode = "12345"
            address.lon = 127.0
            address.lat = 37.5
        }
    }

    private fun createOrder(customerId: String): Order {
        return Order(customerId = customerId).apply {
            items.add(OrderItem("Orange", 128.0, 3))
            items.add(OrderItem("Apple", 101.0, 5))
        }
    }
}