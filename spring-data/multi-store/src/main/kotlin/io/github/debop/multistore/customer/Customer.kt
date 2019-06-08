package io.github.debop.multistore.customer

import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id

/**
 * Customer
 * @author debop (Sunghyouk Bae)
 */
@Entity
data class Customer(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,
    var name: String
) {
    @Embedded
    var address: Address = Address()
}

@Embeddable
data class Address(var lon: Double? = null, var lat: Double? = null) {

    var street: String? = null
    var zipcode: String? = null
}