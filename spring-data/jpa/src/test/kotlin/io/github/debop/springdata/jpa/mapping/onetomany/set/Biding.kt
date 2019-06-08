package io.github.debop.springdata.jpa.mapping.onetomany.set

import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption.TRUE
import java.io.Serializable
import java.math.BigDecimal
import java.sql.Timestamp
import javax.persistence.CascadeType.ALL
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity(name = "onetomany_bidding_item")
data class BiddingItem(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,
    var name: String
) : Serializable {

    @OneToMany(cascade = [ALL], orphanRemoval = true)
    @LazyCollection(TRUE)
    val bids: MutableSet<Bid> = hashSetOf()

    fun addBids(vararg bidsToAdd: Bid) {
        bidsToAdd.forEach {
            if (this.bids.add(it)) {
                it.item = this
            }
        }
    }

    fun removeBids(vararg bidsToRemove: Bid) {
        bidsToRemove.forEach {
            if (this.bids.remove(it)) {
                it.item = null
            }
        }
    }
}

@Entity(name = "onetomany_bid")
data class Bid(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,
    var amount: BigDecimal = BigDecimal.ZERO
) : Serializable {

    @ManyToOne(optional = false, fetch = LAZY)
    @JoinColumn(name = "bidding_item_id")
    var item: BiddingItem? = null

    @Transient
    var timestamp: Timestamp? = null
}
