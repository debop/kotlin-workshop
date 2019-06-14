package io.github.debop.lettuce.cache

import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisOperations

/**
 * LettuceSpringCacheApplicationTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 14
 */

@SpringBootTest
class LettuceSpringCacheApplicationTest {

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Autowired
    private lateinit var redisOperations: RedisOperations<Any, Any>

    @Test
    fun `context loading`() {
        cacheManager.shouldNotBeNull()
        redisOperations.shouldNotBeNull()
    }
}