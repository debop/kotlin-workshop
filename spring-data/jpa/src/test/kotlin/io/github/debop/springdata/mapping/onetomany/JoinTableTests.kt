package io.github.debop.springdata.mapping.onetomany

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.MapKeyColumn
import javax.persistence.OneToMany

/**
 * JoinTableTests
 *
 * @author debop
 * @since 19. 6. 4
 */
@DataJpaTest
class JoinTableTests {

    @Entity(name = "onetomany_address")
    data class Address(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long? = null,
        var city: String? = null
    )

    @Entity(name = "onetomany_user")
    data class User(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id", insertable = false, updatable = false)
        var id: Long? = null,

        @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @JoinTable(name = "onetomany_user_address")
        @MapKeyColumn(name = "address_name")
        @ElementCollection(targetClass = Address::class, fetch = FetchType.EAGER)
        val addresses: MutableMap<String, Address> = hashMapOf(),

        @ElementCollection(targetClass = String::class, fetch = FetchType.LAZY)
        @JoinTable(name = "onetomany_user_nicks", joinColumns = [JoinColumn(name = "user_id")])
        val nicknames: MutableSet<String> = hashSetOf()
    )
}