package io.github.debop.jooq

import io.github.debop.jooq.config.CategoryConfiguration
import io.github.debop.jooq.tables.Category.CATEGORY
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureJdbc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * SimpleEntityTests
 * @author debop (Sunghyouk Bae)
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [CategoryConfiguration::class])
@AutoConfigureJdbc
@ComponentScan
class SimpleEntityTests {

    companion object : KLogging()

    @Autowired
    private lateinit var repository: CategoryRepository

    @Autowired
    private lateinit var dslContext: DSLContext

    @Test
    fun `context loading`() {
        repository.shouldNotBeNull()
        dslContext.shouldNotBeNull()
    }

    @Transactional
    @Test
    fun `save and load categories with jdbc and jooq`() {

        repository.deleteAll()

        val cars = Category(null,
                            "Cars",
                            "Anything that has approximately 4 wheels",
                            AgeGroup._3to8)

        val buildings = Category(null, "Buildings", null, AgeGroup._12andOlder)

        // save
        val saved = repository.saveAll(listOf(cars, buildings))

        Output.list(repository.findAll(), "`Cars` and `Buildings` got saved")

        saved.forEach { it.id.shouldNotBeNull() }

        // update one
        buildings.description = "Famous and impressive buildings incl. the 'bike shed'."
        repository.save(buildings)

        Output.list(repository.findAll(), "`Building` has description")

        // query with JOOQ
        val categories = repository.getCategoriesWithAgeGroup(AgeGroup._3to8)

        categories.size shouldEqualTo 1
        categories[0] shouldEqual cars
    }

    @Transactional
    @Test
    fun `using jOOQ DSL`() {

        dslContext.delete(CATEGORY)

        dslContext.insertInto(CATEGORY)
            .columns(CATEGORY.NAME, CATEGORY.DESCRIPTION, CATEGORY.AGE_GROUP)
            .values("Chairs", "의자", AgeGroup._8to12.name)
            .values("Desks", "책상", AgeGroup._3to8.name)
            .execute()

        val records = dslContext
            .select()
            .from(CATEGORY)
            .where(CATEGORY.NAME.isNotNull)
            // .fetchInto(CATEGORY)
            .fetchInto(io.github.debop.jooq.tables.pojos.Category::class.java)

        records.size shouldEqualTo 2
        records.forEach {
            logger.debug { it }
        }
    }
}