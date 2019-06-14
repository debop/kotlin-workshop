package io.github.debop.redis.examples.springdata.commands

import io.github.debop.redis.examples.springdata.SpringDataRedisApplication
import mu.KLogging
import org.amshove.kluent.shouldBeInRange
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit
import org.springframework.data.redis.core.GeoOperations
import org.springframework.data.redis.core.RedisOperations

/**
 * GeoOperationsTest
 *
 * @author debop
 * @since 19. 6. 14
 */
@SpringBootTest(classes = [SpringDataRedisApplication::class])
class GeoOperationsTest {

    companion object : KLogging()

    @Autowired
    private lateinit var operations: RedisOperations<String, String>

    private lateinit var geoOperations: GeoOperations<String, String>

    @BeforeEach
    fun setup() {
        geoOperations = operations.opsForGeo()

        geoOperations.add("Sicily", Point(13.361389, 38.115556), "Arigento")
        geoOperations.add("Sicily", Point(15.087269, 37.502669), "Catania")
        geoOperations.add("Sicily", Point(13.583333, 37.316667), "Palermo")
    }

    @Test
    fun `find member by radius`() {
        val byDistance =
            geoOperations.radius("Sicily", "Palermo", Distance(100.0, DistanceUnit.KILOMETERS))

        byDistance.shouldNotBeNull()
        byDistance.content.size shouldEqualTo 2

        byDistance.map { it.content.name }.shouldContainSame(listOf("Arigento", "Palermo"))

        val greaterDistance =
            geoOperations.radius("Sicily", "Palermo", Distance(200.0, DistanceUnit.KILOMETERS))

        greaterDistance.shouldNotBeNull()
        greaterDistance.content.size shouldEqualTo 3

        greaterDistance.map { it.content.name }.shouldContainSame(listOf("Arigento", "Palermo", "Catania"))
    }

    @Test
    fun `lookup points within a circle around coordinates`() {
        val circle = Circle(Point(13.5833333, 37.316667), Distance(100.0, DistanceUnit.KILOMETERS))
        val result = geoOperations.radius("Sicily", circle)!!

        result.content.size shouldEqualTo 2
        result.content.map { it.content.name }.shouldContainSame(listOf("Arigento", "Palermo"))
    }

    @Test
    fun `calculate the distance between two geo-index members`() {
        val distance = geoOperations.distance("Sicily", "Catania", "Palermo", DistanceUnit.KILOMETERS)
        distance.shouldNotBeNull()
        distance.value shouldBeInRange 130.0..140.0
    }

    @Test
    fun `get geo-hash`() {
        val geohashes = geoOperations.hash("Sicily", "Catania", "Palermo")

        logger.debug { "geohashes=${geohashes}" }
        geohashes.shouldNotBeNull()
        geohashes.size shouldEqualTo 2
        geohashes shouldContainSame listOf("sqdtr74hyu0", "sq9sm1716e0")
    }
}