package io.github.debop.springdata.mapping.onetomany.list

import com.querydsl.jpa.impl.JPAQuery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Repository
interface OneToOneUserRepository : JpaRepository<User, Long>

@Repository
interface FatherRepository : JpaRepository<Father, Long>

@Repository
interface OrderRepository : JpaRepository<Order, Long>, OrderRepositoryExtensions

interface OrderRepositoryExtensions {

    fun findAllWithInnerJoins(): MutableList<Order>
}

class OrderRepositoryImpl : OrderRepositoryExtensions {

    @PersistenceContext
    lateinit var em: EntityManager

    // NOTE: Order가 존재하지만, OrderItem 없는 경우는 빈 결과가 온다. (left outer join 이 기본인 이유이다)
    /*
        select
            order0_.order_id as order_id1_11_,
            order0_.no as no2_11_
        from
            onetomany_order order0_
        inner join
            onetomany_order_item items1_
                on order0_.order_id=items1_.order_id
        order by
            items1_.name asc
    */
    override fun findAllWithInnerJoins(): MutableList<Order> {
        val order = QOrder.order
        val item = QOrderItem.orderItem

        return JPAQuery<Order>(em).from(order)
            .innerJoin(order.items, item)
            .orderBy(item.name.asc())
            .fetch()
    }
}