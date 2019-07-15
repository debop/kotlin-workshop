package org.javers.core.model

/**
 * DummyNetworkAddress
 *
 * @author debop
 * @since 19. 7. 15
 */
class DummyNetworkAddress {

    enum class Version { IPv4, IPv6 }

    var address: String? = null
    var version: Version? = null
}