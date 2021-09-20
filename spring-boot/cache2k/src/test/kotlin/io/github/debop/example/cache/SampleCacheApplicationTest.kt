package io.github.debop.example.cache

import io.github.debop.example.cache.domain.CountryRepository
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager

@SpringBootTest
class SampleCacheApplicationTest {

    @Autowired
    lateinit var cacheManager: CacheManager

    @Autowired
    lateinit var countryRepository: CountryRepository

    @Test
    fun `validate cache`() {
        val cache = cacheManager.getCache("countries")
        cache.shouldNotBeNull()

        cache.clear()
        cache.get("BE").shouldBeNull()

        val be = countryRepository.findByCode("BE")
        cache.get("BE")?.get() shouldBeEqualTo be

        val be2 = countryRepository.findByCode("BE")
        cache.get("BE")?.get() shouldBeEqualTo be2
    }
}