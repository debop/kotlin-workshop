package io.github.debop.redisson.springdata.repositories

import io.github.debop.kotlin.tests.asserts.isEqualTo
import io.github.debop.kotlin.tests.asserts.isFalse
import io.github.debop.kotlin.tests.asserts.isTrue
import mu.KLogging
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Example
import org.springframework.data.domain.PageRequest
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.core.RedisOperations

/**
 * PersonRepositoryTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 13
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PersonRepositoryTest {

    companion object: KLogging() {
        val CHARSET = Charsets.UTF_8

        val eddard = Person("eddard", "stark", Gender.MALE)
        val robb = Person("robb", "stark", Gender.MALE)
        val sansa = Person("sansa", "stark", Gender.FEMALE)
        val arya = Person("arya", "stark", Gender.FEMALE)
        val bran = Person("bran", "stark", Gender.MALE)
        val rickon = Person("rickon", "stark", Gender.MALE)
        val jon = Person("jon", "snow", Gender.MALE)
        val debop = Person("sunghyouk", "bae", Gender.MALE)

        val allPeople = listOf(eddard, robb, sansa, arya, bran, rickon, jon, debop)
    }

    @Autowired
    private lateinit var operations: RedisOperations<String, String>

    @Autowired
    private lateinit var repository: PersonRepository

    @BeforeEach
    @AfterEach
    fun setup() {
        operations.execute { conn ->
            conn.flushDb()
            "OK"
        }
    }

    @Test
    fun `context loading`() {
        repository.shouldNotBeNull()
    }

    @Test
    fun `save single entity`() {
        repository.save(eddard)

        operations.execute { conn ->
            val key = "persons:${eddard.id}"
            conn.exists(key.toByteArray(CHARSET))
        }!!.shouldBeTrue()
    }

    @Test
    fun `find by single property`() {
        flushTestUsers()

        val starks = repository.findByLastname(eddard.lastname!!)

        logger.debug { "starks=$starks" }
        assertThat(starks).containsOnly(eddard, robb, sansa, arya, bran, rickon)
    }

    @Test
    fun `find by multiple properties`() {
        flushTestUsers()

        val aryaStark = repository.findByFirstnameAndLastname(arya.firstname!!, arya.lastname!!)

        assertThat(aryaStark).containsOnly(arya)
    }

    @Test
    fun `find by multiple properties using or`() {
        flushTestUsers()

        val aryaAndJon = repository.findByFirstnameOrLastname(arya.firstname!!, jon.lastname!!)

        assertThat(aryaAndJon).containsOnly(arya, jon)
    }

    @Test
    fun `find by query by example`() {
        val example = Example.of(Person(lastname = "stark"))

        flushTestUsers()

        val starks = repository.findAll(example)
        assertThat(starks).contains(arya, eddard).doesNotContain(jon)
    }

    @Test
    fun `find by returing page`() {
        flushTestUsers()

        val page1 = repository.findPersonByLastname(eddard.lastname!!, PageRequest.of(0, 5))

        page1.numberOfElements isEqualTo 5
        page1.totalElements isEqualTo 6
        page1.hasNext().isTrue()

        val page2 = repository.findPersonByLastname(eddard.lastname!!, PageRequest.of(1, 5))

        page2.numberOfElements isEqualTo 1
        page2.totalElements isEqualTo 6
        page2.hasNext().isFalse()
    }

    @Test
    fun `find by embedded property`() {
        val winterfell = Address(country = "the north", city = "winterfell")
        debop.address = winterfell

        flushTestUsers()

        val results = repository.findByAddress_City(winterfell.city!!)
        assertThat(results).containsOnly(debop)
    }

    /**
     *  Find entity by a [GeoIndexed] property on an embedded entity.
     */
    @Disabled("Redisson Geo Operation 에 문제가 있는 것 같음")
    @Test
    fun `find by geo location property`() {
        val winterfell = Address(country = "the north", city = "winterfell", location = Point(52.9541053, -1.2401016))
        debop.address = winterfell

        val casterlystein = Address(country = "Westerland", city = "Casterlystein", location = Point(51.5287352, -0.3817819))
        robb.address = casterlystein

        flushTestUsers()

        val innerCircle = Circle(Point(51.8911912, -0.4979756), Distance(50.0, Metrics.KILOMETERS))
        val smallResults = repository.findByAddress_LocationWithin(innerCircle)
        assertThat(smallResults).containsOnly(debop)

        val biggerCircle = Circle(Point(51.8911912, -0.4979756), Distance(200.0, Metrics.KILOMETERS))
        val biggerResults = repository.findByAddress_LocationWithin(biggerCircle)
        assertThat(biggerResults).hasSize(2).contains(robb, eddard)
    }

    @Test
    fun `use references to store data to other objects`() {
        flushTestUsers()

        eddard.children = mutableListOf(jon, robb, sansa, arya, bran, rickon)

        repository.save(eddard)

        assertThat(repository.findById(eddard.id!!)).hasValueSatisfying {
            assertThat(it.children).contains(jon, robb, sansa, arya, bran, rickon)
        }

        repository.deleteAll(listOf(robb, jon))

        assertThat(repository.findById(eddard.id!!)).hasValueSatisfying {
            assertThat(it.children).contains(sansa, arya, bran, rickon)
            assertThat(it.children).doesNotContain(robb, jon)
        }
    }

    private fun flushTestUsers() {
        repository.saveAll(allPeople)
    }
}