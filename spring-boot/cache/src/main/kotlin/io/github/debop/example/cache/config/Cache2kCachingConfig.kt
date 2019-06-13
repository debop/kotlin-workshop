package io.github.debop.example.cache.config

import io.github.debop.example.cache.domain.Country
import io.github.debop.example.cache.listener.Slf4jCacheEntryCreatedListener
import org.cache2k.Cache2kBuilder
import org.cache2k.extra.spring.SpringCache2kCacheManager
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit
import java.util.function.Function

/**
 * Cache2kCachingConfig
 *
 * @author debop
 * @since 19. 6. 13
 */
@Configuration
@EnableCaching
class Cache2kCachingConfig {

    @Suppress("UNCHECKED_CAST")
    @Bean
    fun cacheManager(): CacheManager {
        return SpringCache2kCacheManager()
            .defaultSetup { it.entryCapacity(200) }
            .addCaches(
                Function {
                    val builder = it as Cache2kBuilder<String, Country>
                    builder.name("countries")
                        .permitNullValues(true)
                        .addListener(Slf4jCacheEntryCreatedListener())
                },
                Function {
                    it.name("test1")
                        .expireAfterWrite(30, TimeUnit.SECONDS)
                        .entryCapacity(1000)
                        .permitNullValues(true)
                }
            )
    }
}