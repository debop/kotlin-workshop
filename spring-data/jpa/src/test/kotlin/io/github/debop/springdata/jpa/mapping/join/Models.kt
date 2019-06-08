package io.github.debop.springdata.jpa.mapping.join

import org.hibernate.annotations.Cascade
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.MapKeyColumn
import javax.persistence.OneToMany
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.SecondaryTable


interface JoinUserRepository : JpaRepository<JoinUser, Long>

interface JoinCustomerRepository : JpaRepository<JoinCustomer, Long>

@Embeddable
data class Address(
    var street: String? = null,
    var city: String? = null,
    var zipcode: String? = null
)

@Entity(name = "join_address_entity")
data class AddressEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    var id: Long? = null,
    var street: String? = null,
    var city: String? = null,
    var zipcode: String? = null
)

@Entity(name = "join_user")
data class JoinUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null,
    var name: String
) {

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(name = "join_user_address_map",
               joinColumns = [JoinColumn(name = "user_id")],
               inverseJoinColumns = [JoinColumn(name = "address_id")])
    @MapKeyColumn(name = "address_name")
    @ElementCollection(targetClass = AddressEntity::class, fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)  // left outer join 으로 가져옵니다.
    val addresses: MutableMap<String, AddressEntity> = hashMapOf()

    @JoinTable(name = "join_user_nicknames", joinColumns = [JoinColumn(name = "user_id")])
    @ElementCollection(targetClass = String::class, fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    val nicknames: MutableSet<String> = hashSetOf()
}

@Entity(name = "join_customer")
@SecondaryTable(name = "join_customer_address", pkJoinColumns = [PrimaryKeyJoinColumn(name = "customer_id")])
data class JoinCustomer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    var id: Long? = null,
    var name: String,
    var email: String? = null
) {

    // @SecondaryTable 에 정의된 table을 사용합니다.
    @Embedded
    @AttributeOverrides(AttributeOverride(name = "street", column = Column(name = "street", table = "join_customer_address")),
                        AttributeOverride(name = "city", column = Column(name = "city", table = "join_customer_address")),
                        AttributeOverride(name = "zipcode", column = Column(name = "zipcode", table = "join_customer_address")))
    val address = Address()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(name = "join_customer_address_map",
               joinColumns = [JoinColumn(name = "customer_id")],
               inverseJoinColumns = [JoinColumn(name = "address_id")])
    @MapKeyColumn(name = "address_name")
    @ElementCollection(targetClass = AddressEntity::class, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT) // Customer 를 가져온 후, 해당 Address를 따로 가져온다
    val addresses: MutableMap<String, AddressEntity> = hashMapOf()

    @CreatedDate
    var createdAt: LocalDateTime? = null

    @LastModifiedDate
    var lastModifiedAt: LocalDateTime? = null
}
