package org.javers.core.model

/**
 * DummyAddress
 *
 * @author debop
 * @since 19. 7. 15
 */
data class DummyAddress(val city: String, val street: String? = null): AbstractDummyAddress() {

    companion object {
        @JvmField
        var staticInt: Int? = null
    }

    var kind: Kind? = null
    var networkAddress: DummyNetworkAddress? = null
    @Transient var someTransientField: Int? = null


    enum class Kind { HOME, OFFICE }


}