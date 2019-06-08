package io.github.debop.multistore.order

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * OrderRepository
 * @author debop (Sunghyouk Bae)
 */
@Repository
interface OrderRepository : MongoRepository<Order, String> {

    fun findByCustomerId(customerId: String): List<Order>

}