package io.github.debop.springdata.jpa.mapping.manytoone

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeEmpty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class ManyToOneTests : AbstractDataJpaTest() {

    @Autowired
    private lateinit var breweryRepo: BreweryRepository
    @Autowired
    private lateinit var beerRepo: BeerRepository

    @Autowired
    private lateinit var jugRepo: JugRepository
    @Autowired
    private lateinit var jugMeterRepo: JugMeterRepository

    @Test
    fun `many-to-one unidirectional`() {
        val jug = Jug(name = "Jug Summer Camp")
        val emmanuel = JugMeter(name = "Emmanuel Bernard")
        emmanuel.memberOf = jug

        val jerome = JugMeter(name = "Jerome")
        jerome.memberOf = jug

        jugRepo.save(jug)
        jugMeterRepo.save(emmanuel)
        jugMeterRepo.save(jerome)
        flushAndClear()

        val eloaded = jugMeterRepo.findByIdOrNull(emmanuel.id)!!
        eloaded shouldEqual emmanuel
        eloaded.memberOf shouldEqual jug

        jugMeterRepo.delete(eloaded)
        flushAndClear()

        val jloaded = jugMeterRepo.findByIdOrNull(jerome.id)!!
        jloaded shouldEqual jerome
        jloaded.memberOf shouldEqual jug

        jugMeterRepo.deleteAll()
        flushAndClear()

        jugRepo.findAll().shouldNotBeEmpty()  // @ManyToOne 의 cascade 가 없고, unidirectional 이기 때문이다.

        jugRepo.deleteById(jug.id!!)
        jugRepo.findAll().shouldBeEmpty()
    }

    @Test
    fun `many-to-one bidirectional`() {
        val brewery = Brewery(name = "Berlin")
        val beer1 = Beer(name = "Normal")
        val beer2 = Beer(name = "Black")
        val beer3 = Beer(name = "ColdBrew")

        brewery.beers.addAll(listOf(beer1, beer2, beer3))
        // brewerry 가 nonnull 이므로 꼭 저장하기 전에 assign 해주어야 한다. (bidirectional)
        beer1.brewery = brewery
        beer2.brewery = brewery
        beer3.brewery = brewery

        breweryRepo.save(brewery)
        flushAndClear()

        val loaded = breweryRepo.findByIdOrNull(brewery.id)!!
        loaded shouldEqual brewery
        loaded.beers shouldContainAll brewery.beers

        val beerToRemove = loaded.beers.first()
        loaded.beers.remove(beerToRemove)
        beerRepo.delete(beerToRemove)
        breweryRepo.save(loaded)

        flushAndClear()

        breweryRepo.delete(loaded)
        flushAndClear()

        breweryRepo.findAll().shouldBeEmpty()
    }

    @Test
    fun `many-to-one bidirectional with cascade all`() {

        val salesForce = SalesForce(name = "BMW Korea")
        val salesGuy1 = SalesGuy(name = "debop")
        val salesGuy2 = SalesGuy(name = "smith")
        val salesGuy3 = SalesGuy(name = "james")

        salesForce.addGuys(salesGuy1, salesGuy2, salesGuy3)

        // Save sales guys by cascade
        em.persist(salesForce)
        flushAndClear()

        val loaded = em.find(SalesForce::class.java, salesForce.id)!!
        loaded shouldEqual salesForce
        loaded.salesGuys.size shouldEqual salesForce.salesGuys.size

        val guyToRemove = loaded.salesGuys.last()
        loaded.removeGuys(guyToRemove)
        em.remove(guyToRemove)  // guyToRemove 를 삭제하기 전에 salesForce = null 을 설정해야 삭제가 진행됩니다.
        flushAndClear()

        val loaded2 = em.find(SalesForce::class.java, salesForce.id)!!
        loaded2 shouldEqual salesForce
        loaded2.salesGuys.size shouldEqual salesForce.salesGuys.size - 1
    }

}