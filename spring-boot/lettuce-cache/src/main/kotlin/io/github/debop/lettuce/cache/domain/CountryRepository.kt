package io.github.debop.lettuce.cache.domain

import mu.KLogging
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

/**
 * CountryRepository
 *
 * @author debop
 * @since 19. 6. 13
 */
@Component
@CacheConfig(cacheNames = ["countries"])
class CountryRepository {

    companion object: KLogging()

    @Cacheable
    fun findByCode(code: String): Country {
        logger.info { "---> Loading country with code `$code`" }
        return Country(code)
    }
}