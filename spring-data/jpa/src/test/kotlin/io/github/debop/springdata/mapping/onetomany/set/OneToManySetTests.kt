package io.github.debop.springdata.mapping.onetomany.set

import io.github.debop.springdata.AbstractDataJpaTest
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal

/**
 * OneToManySetTests
 *
 * @author debop
 * @since 19. 6. 5
 */
class OneToManySetTests : AbstractDataJpaTest() {

    @Autowired
    private lateinit var biddingItemRepo: BiddingItemRepository

    @Autowired
    private lateinit var productRepo: ProductRepository

    @Test
    fun `one-to-many set with bidirectional`() {
        val item = BiddingItem(name = "TV")
        val bid1 = Bid(amount = BigDecimal(100.0))
        val bid2 = Bid(amount = BigDecimal(200.0))
        val bid3 = Bid(amount = BigDecimal(300.0))
        item.addBids(bid1, bid2, bid3)

        item.bids.size shouldEqualTo 3

        biddingItemRepo.saveAndFlush(item)
        clear()

        val loaded = biddingItemRepo.findByIdOrNull(item.id)
        loaded.shouldNotBeNull()
        loaded shouldEqual item
        loaded.bids.size shouldEqualTo item.bids.size

        val bidToRemoved = loaded.bids.first()
        loaded.removeBids(bidToRemoved)
        biddingItemRepo.saveAndFlush(loaded)
        clear()

        val loaded2 = biddingItemRepo.findByIdOrNull(item.id)
        loaded2.shouldNotBeNull()
        loaded2 shouldEqual item
        loaded2.bids.size shouldEqualTo item.bids.size - 1
        loaded2.bids shouldNotContain bidToRemoved

        biddingItemRepo.delete(loaded2)
        biddingItemRepo.flush()
        clear()

        biddingItemRepo.findAll().shouldBeEmpty()
    }

    @Test
    fun `one-to-many with emddeddable component`() {
        val product = Product(name = "Car")
        val image1 = ProductImage(name = "front")
        val image2 = ProductImage("interior")
        val image3 = ProductImage("engine room")

        product.addImages(image1, image2, image3)
        productRepo.saveAndFlush(product)
        clear()

        val loaded = productRepo.findByIdOrNull(product.id)
        loaded.shouldNotBeNull()
        loaded shouldEqual product
        loaded.images.size shouldEqualTo product.images.size

        val imageToRemove = loaded.images.first()
        loaded.removeImages(imageToRemove)
        productRepo.saveAndFlush(loaded)
        clear()

        val loaded2 = productRepo.findByIdOrNull(product.id)
        loaded2.shouldNotBeNull()
        loaded2 shouldEqual product
        loaded2.images.size shouldEqualTo product.images.size - 1

        productRepo.delete(loaded2)
        productRepo.flush()
        clear()

        productRepo.existsById(product.id!!).shouldBeFalse()
    }
}