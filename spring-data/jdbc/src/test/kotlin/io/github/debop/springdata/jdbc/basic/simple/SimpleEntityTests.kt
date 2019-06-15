package io.github.debop.springdata.jdbc.basic.simple

import io.github.debop.springdata.jdbc.basic.Output
import io.github.debop.springdata.jdbc.basic.aggregate.AgeGroup
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@AutoConfigureJdbc
@Transactional
@SpringBootTest(classes = [CategoryConfiguration::class])
class SimpleEntityTests(@Autowired val repository: CategoryRepository) {

    @Test
    fun `exercise repository for simple entity`() {

        // create some categories
        val cars = repository.save(Category("Cars",
                                            "Anything that has approximately 4 wheels",
                                            AgeGroup._3to8))
        val buildings = repository.save(Category("Buildings",
                                                 null,
                                                 AgeGroup._12andOlder))

        // save categories
        Output.list(repository.findAll(), "`Cars` and `Buildings` got saved")

        cars.id.shouldNotBeNull()
        buildings.id.shouldNotBeNull()

        // update one
        buildings.description = "Famous and impressive buildings incl. the 'bike shed'."
        repository.save(buildings)
        Output.list(repository.findAll(), "`Buildings` has a description")

        // delete stuff again
        repository.delete(cars)
        Output.list(repository.findAll(), "`Cars` is gone.")
    }
}