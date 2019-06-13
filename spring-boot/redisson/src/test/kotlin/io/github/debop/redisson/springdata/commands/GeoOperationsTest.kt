package io.github.debop.redisson.springdata.commands

import mu.KLogging
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands.DistanceUnit.KILOMETERS
import org.springframework.data.redis.core.GeoOperations
import org.springframework.data.redis.core.RedisOperations
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * GeoOperationsTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 13
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
class GeoOperationsTest {

    companion object: KLogging()

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
    fun `context loading`() {
        operations.shouldNotBeNull()
        geoOperations.shouldNotBeNull()
    }

    @Test
    fun `radius by member`() {
        val byDistance = geoOperations.radius("Sicily",
                                              "Palermo",
                                              Distance(100.0, KILOMETERS))
        byDistance!!.content.size shouldEqualTo 2
        // byDistance.map { it.content.name } shouldContainAll listOf("Arigento", "Palermo")

        byDistance.forEach {
            logger.debug { "byDistance = ${it.content}" }
        }
    }
}