package io.github.debop.redis.examples.cache

import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager

/**
 * RedisCacheManagerAutoConfigurationTest
 *
 * @author debop
 * @since 19. 6. 14
 */
@SpringBootTest(classes = [RedisCacheApplication::class],
                properties = ["spring.cache.type=redis"])
class RedisCacheManagerAutoConfigurationTest {

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Test
    fun `context loading`() {
        cacheManager.shouldNotBeNull()
    }

    @Test
    fun `get cache instance`() {
        cacheManager.getCache("redis:cache").shouldNotBeNull()
    }
}