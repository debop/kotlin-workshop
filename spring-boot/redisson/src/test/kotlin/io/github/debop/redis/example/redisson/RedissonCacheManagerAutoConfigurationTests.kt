package io.github.debop.redis.example.redisson

import io.github.debop.redis.example.AbstractRedisTests
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * RedissonCacheManagerAutoConfigurationTests
 * @author debop (Sunghyouk Bae)
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [RedissonApplication::class],
                properties = [
                    "spring.cache.type=redis"
                ])
class RedissonCacheManagerAutoConfigurationTests : AbstractRedisTests() {

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Test
    fun `context loading`() {
        cacheManager.shouldNotBeNull()
    }

    @Test
    fun `get cache instance`() {
        val cache = cacheManager.getCache("redis:cache")
        cache.shouldNotBeNull()
    }
}