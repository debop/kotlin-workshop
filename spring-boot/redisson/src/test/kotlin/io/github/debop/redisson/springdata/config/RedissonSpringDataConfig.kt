package io.github.debop.redisson.springdata.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.spring.data.connection.RedissonConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * RedissonSpringDataConfig
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 13
 */
@Configuration
class RedissonSpringDataConfig {

    @Bean
    fun redissonConnectionFactory(redisson: RedissonClient): RedissonConnectionFactory {
        return RedissonConnectionFactory(redisson)
    }

    @Bean(destroyMethod = "shutdown")
    fun redisson(@Value("\${spring.redis.url}") address: String): RedissonClient {
        val config = Config()

        config
            .useSingleServer()
            .setAddress(address)

        return Redisson.create(config)
    }
}