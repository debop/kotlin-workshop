package io.github.debop.springdata.jpa.mapping.unidirection

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

/**
 * UnidirectionTests
 * @author debop (Sunghyouk Bae)
 */
class UnidirectionTests : AbstractDataJpaTest() {

    @Autowired
    private lateinit var cloudRepository: CloudRepository

    @Autowired
    private lateinit var snowflakeRepository: SnowflakeRepository

    @Test
    fun `one-to-many unidirection`() {
        val sf1 = Snowflake(name = "sf1").apply { description = "Snowflake 1" }
        val sf2 = Snowflake(name = "sf2").apply { description = "Snowflake 2" }

        val cloud = Cloud(kind = "cloud", length = 23.0)
        cloud.producedSnowflakes.add(sf1)
        cloud.producedSnowflakes.add(sf2)

        cloudRepository.saveAndFlush(cloud)
        clear()

        var loaded = cloudRepository.findByIdOrNull(cloud.id)

        loaded.shouldNotBeNull()
        loaded shouldEqual cloud
        loaded.producedSnowflakes.size shouldEqualTo 2

        val sfToRemove = loaded.producedSnowflakes.first()
        val sf3 = Snowflake(name = "sf3").apply { description = "Snowflake 3" }
        loaded.producedSnowflakes.remove(sfToRemove)
        loaded.producedSnowflakes.add(sf3)

        snowflakeRepository.delete(sfToRemove)
        cloudRepository.saveAndFlush(loaded)
        clear()

        snowflakeRepository.count() shouldEqualTo 2

        loaded = cloudRepository.findByIdOrNull(cloud.id)

        loaded.shouldNotBeNull()
        loaded shouldEqual cloud
        loaded.producedSnowflakes.size shouldEqualTo 2


    }
}