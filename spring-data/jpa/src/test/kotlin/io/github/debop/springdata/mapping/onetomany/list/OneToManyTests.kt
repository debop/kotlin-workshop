package io.github.debop.springdata.mapping.onetomany.list

import io.github.debop.springdata.AbstractDataJpaTest
import org.amshove.kluent.`should contain all`
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate


class OneToManyTests : AbstractDataJpaTest() {

    @Autowired
    private lateinit var userRepo: OneToOneUserRepository

    @Autowired
    private lateinit var fatherRepo: FatherRepository

    @Autowired
    private lateinit var orderRepo: OrderRepository


    @Test
    fun `one-to-many association with map`() {
        val user = User(name = "Debop")
        user.nicknames.add("debop")
        user.nicknames.add("debop68")

        user.addresses["Home"] = Address(city = "Hanam")
        user.addresses["Office"] = Address(city = "Jamsil")

        userRepo.saveAndFlush(user)
        clear()

        val loaded = userRepo.findByIdOrNull(user.id!!)

        loaded.shouldNotBeNull()
        loaded shouldEqual user

        userRepo.deleteById(loaded.id!!)
        userRepo.flush()
        clear()

        userRepo.findAll().shouldBeEmpty()
    }

    @Test
    fun `one-to-many mappedBy for show who is owner`() {
        val order = Order(no = "12345")
        val item1 = OrderItem(name = "Item1")
        val item2 = OrderItem(name = "Item2")
        order.addItems(item1, item2)

        orderRepo.saveAndFlush(order)
        clear()

        val loaded = orderRepo.findByIdOrNull(order.id)
        loaded.shouldNotBeNull()
        loaded shouldEqual order

        // select count(id) from order_item
        loaded.items.size shouldEqual order.items.size

        loaded.items.forEach {
            log.debug("$it")
        }
        orderRepo.deleteAll()
        orderRepo.flush()
        clear()
    }

    @Test
    fun `one-to-many unidirectional`() {
        val father = Father(name = "이성계")
        val child1 = Child(name = "방원", birthday = LocalDate.of(1390, 2, 10))
        val child2 = Child(name = "방석", birthday = LocalDate.of(1400, 1, 5))
        val child3 = Child(name = "방번", birthday = LocalDate.of(1380, 10, 5))

        father.orderedChildren.addAll(listOf(child1, child2, child3))
        fatherRepo.saveAndFlush(father)
        clear()

        val loaded = fatherRepo.findByIdOrNull(father.id)

        loaded.shouldNotBeNull()
        loaded shouldEqual father
        loaded.orderedChildren `should contain all` listOf(child1, child2, child3)

        loaded.orderedChildren.removeAt(0)
        fatherRepo.saveAndFlush(loaded)
        clear()

        val loaded2 = fatherRepo.findByIdOrNull(father.id)
        loaded2.shouldNotBeNull()
        loaded2 shouldEqual father
        loaded2.orderedChildren.size shouldEqual 2

        fatherRepo.delete(loaded2)
        fatherRepo.flush()
        clear()

        fatherRepo.findAll().shouldBeEmpty()
    }
}