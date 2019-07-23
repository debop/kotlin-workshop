package io.github.debop.lettuce.cache.config

import io.github.debop.redis.serializer.FstRedisSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * LettuceConfiguration
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 14
 */
@EnableCaching
@Configuration
class LettuceConfiguration {

    @Value("\${spring.redis.host}")
    lateinit var redisHost: String

    @Value("\${spring.redis.port}")
    var redisPort: Int = 6379

    @Bean
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        return RedisCacheManager.builder(connectionFactory).build().apply {
            isTransactionAware = true
            // cacheConfigurations
        }
    }

    @Bean
    fun lettuceConnectionFactory(): LettuceConnectionFactory {
        val configuration = RedisStandaloneConfiguration(redisHost, redisPort)
        return LettuceConnectionFactory(configuration)
    }

    @Bean
    @ConditionalOnMissingBean(name = ["redisTemplate"])
    @Primary
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        return RedisTemplate<Any, Any>().apply {
            setConnectionFactory(connectionFactory)
            setDefaultSerializer(FstRedisSerializer.INSTANCE)
            keySerializer = StringRedisSerializer.UTF_8
            valueSerializer = FstRedisSerializer.INSTANCE
        }
    }

}