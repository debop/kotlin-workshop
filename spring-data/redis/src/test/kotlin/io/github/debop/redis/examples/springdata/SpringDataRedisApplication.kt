package io.github.debop.redis.examples.springdata

import io.github.debop.kotlin.tests.containers.RedisServer
import io.github.debop.redis.serializer.FstRedisSerializer
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.WebApplicationType.NONE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.StringRedisSerializer
import javax.annotation.PreDestroy

/**
 * SpringDataRedisApplication
 *
 * @author debop
 * @since 19. 6. 14
 */
@EnableRedisRepositories
@SpringBootApplication
class SpringDataRedisApplication {

    companion object: KLogging() {
        val redisServer = RedisServer()
    }

    @Autowired
    private lateinit var factory: RedisConnectionFactory

    @PreDestroy
    fun flushTestDb() {
        factory.connection.flushDb()
    }

    /**
     * Entity 를 Redis 에 저장 시 FST Serialization을 사용하도록 합니다.
     *
     * @param factory
     * @return
     */
    @Bean
    fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, Any> {
        logger.info { "Create RedisTemplate using Fst redis serializer for value !!!" }
        return RedisTemplate<String, Any>().apply {
            setConnectionFactory(factory)
            keySerializer = StringRedisSerializer.UTF_8
            valueSerializer = FstRedisSerializer.INSTANCE
        }
    }
}

fun main(vararg args: String) {
    runApplication<SpringDataRedisApplication>(*args) {
        webApplicationType = NONE
    }
}