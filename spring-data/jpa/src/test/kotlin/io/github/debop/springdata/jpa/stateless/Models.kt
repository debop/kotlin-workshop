package io.github.debop.springdata.jpa.stateless

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.CascadeType.ALL
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

/**
 * Models
 * @author debop (Sunghyouk Bae)
 */

@Entity
@DynamicInsert
@DynamicUpdate
data class StatelessEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String
) {
    var firstname: String? = null
    var lastname: String? = null
    var age: Int? = null
    var street: String? = null
    var city: String? = null
    var zipcode: String? = null
}

@Entity
data class StatelessMaster(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String
) {

    @OneToMany(mappedBy = "master", cascade = [ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    val details: MutableList<StatelessDetail> = arrayListOf()
}

@Entity
data class StatelessDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "master_id")
    var master: StatelessMaster? = null
) 