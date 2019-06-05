package io.github.debop.springdata.mapping.manytoone

import io.github.debop.springdata.AbstractDataJpaTest
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class ManyToOneTests : AbstractDataJpaTest() {

    @Autowired
    private lateinit var breweryRepo: BreweryRepository
    @Autowired
    private lateinit var beerRepo: BeerRepository

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
    }
}