package io.github.debop.springdata.mapping.onetomany.list

import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption.EXTRA
import java.time.LocalDate
import javax.persistence.CascadeType.ALL
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToOne
import javax.persistence.MapKeyColumn
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.persistence.OrderColumn

@Entity(name = "onetomany_address")
data class Address(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var city: String
)

@Entity(name = "onetomany_user")
@DynamicInsert
@DynamicUpdate
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", insertable = false, updatable = false)
    var id: Long? = null,
    var name: String
) {
    // One To Many 관계를 JoinTable을 이용하여 연결하고, `@MapKeyColumn`을 이용하여 Map 을 구성한다
    @OneToMany(cascade = [ALL], fetch = LAZY)
    @JoinTable(name = "onetomany_user_address")
    @MapKeyColumn(name = "address_name")
    @ElementCollection(targetClass = Address::class, fetch = FetchType.EAGER)
    val addresses: MutableMap<String, Address> = hashMapOf()

    @ElementCollection(targetClass = String::class, fetch = LAZY)
    @JoinTable(name = "onetomany_user_nicks", joinColumns = [JoinColumn(name = "user_id")])
    val nicknames: MutableSet<String> = hashSetOf()
}

@Entity(name = "onetomany_father")
@DynamicInsert
@DynamicUpdate
data class Father(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String
) {
    // One To Many 를 Join Table을 이용하여 Unidirection으로 연결하며, `@OrderColumn`을 이용하여 정렬 작업을 수행한다.
    //
    @OneToMany(cascade = [ALL], fetch = LAZY, orphanRemoval = true)
    @JoinTable(name = "onetomany_father_child")
    @OrderColumn(name = "birthday")
    val orderedChildren: MutableList<Child> = arrayListOf()
}

@Entity(name = "onetomany_child")
data class Child(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String,
    var birthday: LocalDate? = LocalDate.now()
)

@DynamicInsert
@DynamicUpdate
@Entity(name = "onetomany_order")
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    var id: Long? = null,
    var no: String
) {
    // NOTE: Detail entity 삭제 시 delete 구문만 수행하게 됩니다. (set detail.mater=null 을 하지 않는다)
    /*
        Hibernate:
            delete
            from
                onetomany_order_item
            where
                item_id=?
    */
    @OneToMany(mappedBy = "order", cascade = [ALL], fetch = LAZY, orphanRemoval = true)
    @OrderBy("name")
    @LazyCollection(EXTRA)
    val items: MutableSet<OrderItem> = hashSetOf()

    fun addItems(vararg itemsToAdd: OrderItem) {
        itemsToAdd.forEach {
            items.add(it)
            it.order = this
        }
    }

    fun removeItems(vararg itemsToRemove: OrderItem) {
        itemsToRemove.forEach {
            items.remove(it)
            it.order = null
        }
    }
}

@DynamicInsert
@DynamicUpdate
@Entity(name = "onetomany_order_item")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    var id: Long? = null,
    val name: String
) {
    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order? = null
}

@DynamicInsert
@DynamicUpdate
@Entity(name = "onetomany_batch")
data class Batch(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    var id: Long? = null,
    var no: String
) {
    // NOTE: @JoinColumn 을 Master entity에 지정하게 되면 아래와 같이 update / delete 를 수행한다.
    /*
        Hibernate:
            update
                onetomany_batch_item
            set
                batch_id=null
            where
                batch_id=?
                and batch_item_id=?
        Hibernate:
            delete
            from
                onetomany_batch_item
            where
                batch_item_id=?
     */
    @OneToMany(fetch = LAZY)
    @JoinColumn(name = "batch_id")
    @LazyCollection(EXTRA)
    val items: MutableSet<BatchItem> = hashSetOf()

    fun addItems(vararg itemsToAdd: BatchItem) {
        itemsToAdd.forEach {
            items.add(it)
            it.batch = this
        }
    }

    fun removeItems(vararg itemsToRemove: BatchItem) {
        itemsToRemove.forEach {
            items.remove(it)
            it.batch = null
        }
    }
}

@DynamicInsert
@DynamicUpdate
@Entity(name = "onetomany_batch_item")
data class BatchItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_item_id")
    var id: Long? = null,
    val name: String
) {
    @ManyToOne
    @JoinColumn(name = "batch_id")
    var batch: Batch? = null
}
