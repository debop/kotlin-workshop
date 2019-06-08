package io.github.debop.multistore.order

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Order
 * @author debop (Sunghyouk Bae)
 */
@Document
data class Order(
    @Id
    var id: String? = null,
    var customerId: String,
    var orderDate: LocalDateTime = LocalDateTime.now()
) {
    var items: MutableList<OrderItem> = mutableListOf()
}

data class OrderItem(
    var caption: String,
    var price: Double,
    var quantity: Int = 1
)