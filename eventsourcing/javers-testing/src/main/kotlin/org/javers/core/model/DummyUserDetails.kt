package org.javers.core.model

import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.PropertyName

/**
 * DummyUserDetails
 *
 * @author debop
 * @since 19. 7. 16
 */
data class DummyUserDetails(@Id val id: Int = DEFAULT_ID,
                            var dummyAddress: DummyAddress? = null) {

    companion object {
        const val DEFAULT_ID = 1
    }

    var someValue: String? = null
    var isTrue: Boolean? = null
    val addressList: MutableList<DummyAddress> = mutableListOf()
    val interList: MutableList<Int> = mutableListOf()

    @PropertyName("Customized Property")
    var customizedProperty: String? = null

    fun withAddress(street: String, city: String) = apply {
        this.dummyAddress = DummyAddress(city, street)
    }

    fun withAddress() = apply { this.dummyAddress = DummyAddress("city", "street") }

    fun withAddresses(vararg dummyAddresses: DummyAddress) = apply {
        this.addressList.addAll(dummyAddresses)
    }
}