package io.github.debop.springdata.jpa.mapping.associations.onetomany.list

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import mu.KotlinLogging.logger
import org.amshove.kluent.`should contain all`
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate


class OneToManyTests : AbstractDataJpaTest() {

    val log = logger {}

    @Autowired
    private lateinit var userRepo: OneToManyUserRepository
    @Autowired
    private lateinit var fatherRepo: FatherRepository
    @Autowired
    private lateinit var orderRepo: OrderRepository
    @Autowired
    private lateinit var batchRepo: BatchRepository
    @Autowired
    private lateinit var batchItemRepo: BatchItemRepository

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
        loaded shouldBeEqualTo user

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
        loaded shouldBeEqualTo order

        // select count(id) from order_item
        loaded.items.size shouldBeEqualTo order.items.size

        loaded.items.forEach {
            log.debug("$it")
        }

        // Inner Join 을 이용하여 조회한다.
        val loaded2 = orderRepo.findAllWithInnerJoins()
        loaded2.shouldNotBeNull()
        loaded.items.size shouldBeEqualTo order.items.size

        /*
            Hibernate:
                delete
                from
                    onetomany_order_item
                where
                    item_id=?
         */
        loaded.removeItems(item1)
        orderRepo.saveAndFlush(loaded)

        orderRepo.deleteAll()
        orderRepo.flush()
        clear()
    }

    @Test
    fun `one-to-many with @JoinColumn`() {
        val batch = Batch(no = "12345")
        val item1 = BatchItem(name = "Item1")
        val item2 = BatchItem(name = "Item2")
        val item3 = BatchItem(name = "Item3")
        batch.addItems(item1, item2, item3)

        batchRepo.save(batch)
        // cascade 가 없으므로 직접 저장해야 한다
        batchItemRepo.save(item1)
        batchItemRepo.save(item2)
        batchItemRepo.save(item3)
        batchRepo.flush()
        clear()

        val loaded = batchRepo.findByIdOrNull(batch.id)
        loaded.shouldNotBeNull()
        loaded shouldBeEqualTo batch

        loaded.items.size shouldBeEqualTo batch.items.size

        loaded.removeItems(item1)
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
        batchItemRepo.delete(item1)
        batchRepo.saveAndFlush(loaded)
        batchItemRepo.flush()

        loaded.items.size shouldBeEqualTo batch.items.size - 1

        clear()

        val loaded2 = batchRepo.findByIdOrNull(batch.id)!!

        // 이 방식은 관련 item 들을 모두 읽어온 후 하나씩 삭제한다.
        // batchItemRepo.deleteAllByBatch_Id(loaded2.id!!)

        // 이 방식은 batch.id 를 기준으로 item 을 모두 삭제한다.
        batchItemRepo.deleteAllByBatchId(loaded2.id!!)
        batchItemRepo.flush()

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
        loaded shouldBeEqualTo father
        loaded.orderedChildren `should contain all` listOf(child1, child2, child3)

        loaded.orderedChildren.removeAt(0)
        fatherRepo.saveAndFlush(loaded)
        clear()

        val loaded2 = fatherRepo.findByIdOrNull(father.id)
        loaded2.shouldNotBeNull()
        loaded2 shouldBeEqualTo father
        loaded2.orderedChildren.size shouldBeEqualTo 2

        fatherRepo.delete(loaded2)
        fatherRepo.flush()
        clear()

        fatherRepo.findAll().shouldBeEmpty()
    }
}