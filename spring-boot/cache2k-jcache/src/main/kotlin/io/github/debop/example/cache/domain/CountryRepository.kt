package io.github.debop.example.cache.domain

import mu.KLogging
import org.springframework.stereotype.Component
import javax.cache.annotation.CacheResult

/**
 * CountryRepository
 *
 * @author debop
 * @since 19. 6. 13
 */
@Component
class CountryRepository {

    companion object : KLogging()

    @CacheResult(cacheName = "countries")
    fun findByCode(code: String): Country {
        logger.info { "---> Loading country with code `$code`" }
        return Country(code)
    }
}