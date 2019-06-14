package io.github.debop.redis.examples.cache

import mu.KLogging
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate

/**
 * RedisAutoConfigurationTest
 *
 * @author debop
 * @since 19. 6. 14
 */
@SpringBootTest(classes = [RedisCacheApplication::class],
    // application.properties 에서 읽거나, 이렇게 재정의하면 됩니다.
                properties = [
                    "spring.redis.host=\${testcontainers.redis.host}",
                    "spring.redis.port=\${testcontainers.redis.port}",
                    "spring.redis.url=\${testcontainers.redis.url}"
                ])
class RedisAutoConfigurationTest {

    companion object : KLogging()

    @Autowired
    private lateinit var template: StringRedisTemplate

    @Test
    fun `context loading`() {
        template.shouldNotBeNull()
    }
}