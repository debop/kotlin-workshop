package io.github.debop.example.cache

import io.github.debop.example.cache.domain.Country
import io.github.debop.example.cache.domain.CountryRepository
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.cache.Caching

@ExtendWith(SpringExtension::class)
@SpringBootTest
class SampleJCacheApplicationTest {

    @Autowired
    lateinit var countryRepository: CountryRepository

    @Test
    fun `validate jcache`() {
        val provider = Caching.getCachingProvider("org.cache2k.jcache.provider.JCacheProvider")
        val manager = provider.cacheManager

        val cache = manager.getCache<String, Country>("countries")
        cache.shouldNotBeNull()

        cache.clear()
        cache.get("BE").shouldBeNull()

        val be = countryRepository.findByCode("BE")
        cache.get("BE") shouldEqual be
    }
}