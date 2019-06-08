package io.github.debop.multistore

import io.github.debop.multistore.customer.Customer
import io.github.debop.multistore.order.Order
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.mongodb.repository.query.MongoEntityInformation
import org.springframework.data.repository.support.Repositories
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * ApplicationConfigurationTests
 * @author debop (Sunghyouk Bae)
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
class ApplicationConfigurationTests : AbstractMultistoreTests() {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    fun `repositories are assigned to appropriate store`() {

        val repositories = Repositories(context)

        repositories
            .getEntityInformationFor<Customer, Long>(Customer::class.java)
            .shouldBeInstanceOf(JpaEntityInformation::class)

        repositories
            .getEntityInformationFor<Order, String>(Order::class.java)
            .shouldBeInstanceOf(MongoEntityInformation::class)
    }
}