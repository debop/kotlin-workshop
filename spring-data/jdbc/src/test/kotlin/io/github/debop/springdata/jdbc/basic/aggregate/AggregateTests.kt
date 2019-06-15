package io.github.debop.springdata.jdbc.basic.aggregate

import io.github.debop.springdata.jdbc.basic.Output
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@AutoConfigureJdbc
@Transactional
@SpringBootTest(classes = [AggregateConfiguration::class])
class AggregateTests(@Autowired val repository: LegoSetRepository) {

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `exercise somewhat complex entity`() {
        val smallCar = createLegoSet("Small Car 01", 5, 12).apply {
            manual = Manual("Just put all the pieces together in the right order", "Jens Schauder")
            addModel("suv", "SUV with sliding doors.")
            addModel("roadster", "Slick red roadster.")
        }

        repository.save(smallCar)
        var legoSets = repository.findAll()
        Output.list(legoSets, "Original LegoSet")
        checkLegoSets(legoSets, "Just put all the pieces together in the right order", 2)

        smallCar.manual?.text = "Just make it so it looks like a car."
        smallCar.addModel("pickupr", "A pickup truc with some tools in the back.")

        repository.save(smallCar)
        legoSets = repository.findAll()
        Output.list(legoSets, "Updated")
        checkLegoSets(legoSets, "Just make it so it looks like a car.", 3)

        smallCar.manual = Manual("One last attempt: Just build a car! Ok?", "Jens Schauder")

        repository.save(smallCar)
        legoSets = repository.findAll()
        Output.list(legoSets, "Manual replaced")
        checkLegoSets(legoSets, "One last attempt: Just build a car! Ok?", 3)
    }

    @Test
    fun `custom queries`() {
        val smallCars = createLegoSet("Small Car 01", 5, 10).apply {
            manual = Manual("Just put all the pieces together in the right order", "Jens Schauder")
            addModel("SUV", "SUV with sliding doors.")
            addModel("roadster", "Slick red roadster.")
        }

        val f1Racer = createLegoSet("F1 Racer", 6, 15)
        f1Racer.manual = Manual("Build a helicopter or a plane", "M. Shoemaker")
        f1Racer.addModel("F1 Ferrari 2018", "A very fast red car.")

        val constructionVehicles = createLegoSet("Construction Vehicles", 3, 6).apply {
            manual = Manual("Build a Road Roler, a Mobile Crane, a Tracked Dumper, or a Backhoe Loader ", "Bob the Builder")

            addModel("scoop", "A backhoe loader")
            addModel("Muck", "Muck is a continuous tracked dump truck with an added bulldozer blade")
            addModel("lofty", "A mobile crane")
            addModel("roley", "A road roller that loves to make up songs and frequently spins his eyes when he is excited.")
        }
        repository.saveAll(listOf(smallCars, f1Racer, constructionVehicles))

        val report = repository.reportModelForAge(6)
        Output.list(report, "Model Report")

        report.size shouldEqualTo 7
        report.all { it.description != null && it.modelName != null && it.setName != null } shouldEqualTo true

        repository.lowerCaseMapKeys() shouldEqualTo 3
    }

    private fun createLegoSet(name: String, minimumAge: Int, maximumAge: Int): LegoSet {
        return LegoSet().apply {
            this.name = name
            minAge = minimumAge
            maxAge = maximumAge
        }
    }

    private fun checkLegoSets(legoSets: Iterable<LegoSet>, manualText: String, numberOfModels: Int) {
        legoSets
            .map { it.manual!!.text!! to it.models.size }
            .shouldContain(manualText to numberOfModels)
    }
}