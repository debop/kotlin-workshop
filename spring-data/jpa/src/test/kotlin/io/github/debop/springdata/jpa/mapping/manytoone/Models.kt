package io.github.debop.springdata.jpa.mapping.manytoone

import io.github.debop.springdata.jpa.entities.JpaEntity
import org.hibernate.annotations.LazyToOne
import org.hibernate.annotations.LazyToOneOption.PROXY
import javax.persistence.CascadeType.ALL
import javax.persistence.CascadeType.MERGE
import javax.persistence.CascadeType.PERSIST
import javax.persistence.CascadeType.REFRESH
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "manytoone_beer")
data class Beer(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "beer_id")
    override var id: Long? = null,
    var name: String) : JpaEntity<Long> {

    @ManyToOne(fetch = LAZY, optional = false, cascade = [MERGE, PERSIST, REFRESH])
    @JoinColumn(name = "brewery_id", nullable = false)
    @LazyToOne(PROXY)
    var brewery: Brewery? = null
}

@Entity(name = "manytoone_brewery")
data class Brewery(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "brewery_id")
    override var id: Long? = null,
    var name: String) : JpaEntity<Long> {

    @OneToMany(mappedBy = "brewery", cascade = [ALL], fetch = LAZY, orphanRemoval = true)
    val beers: MutableSet<Beer> = hashSetOf()
}

@Entity
data class Jug(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    override var id: Long? = null,
    val name: String
) : JpaEntity<Long>

@Entity
data class JugMeter(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    override var id: Long? = null,
    val name: String) : JpaEntity<Long> {

    @ManyToOne(optional = false, fetch = EAGER)
    @JoinColumn(name = "jug_id")
    var memberOf: Jug? = null
}


@Entity
data class SalesGuy(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    override var id: Long? = null,
    val name: String
) : JpaEntity<Long> {

    @ManyToOne(fetch = LAZY, cascade = [MERGE, PERSIST, REFRESH])
    @JoinColumn(name = "sales_force_id")
    @LazyToOne(PROXY)
    var salesForce: SalesForce? = null
}

@Entity
data class SalesForce(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    override var id: Long? = null,
    val name: String
) : JpaEntity<Long> {

    @OneToMany(mappedBy = "salesForce", cascade = [ALL], fetch = LAZY, orphanRemoval = true)
    val salesGuys: MutableSet<SalesGuy> = hashSetOf()

    fun addGuys(vararg guys: SalesGuy) {
        guys.forEach {
            if (this.salesGuys.add(it)) {
                it.salesForce = this
            }
        }
    }

    fun removeGuys(vararg guys: SalesGuy) {
        guys.forEach {
            if (this.salesGuys.remove(it)) {
                it.salesForce = null
            }
        }
    }
}
