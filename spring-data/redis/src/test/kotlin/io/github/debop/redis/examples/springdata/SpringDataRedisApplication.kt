package io.github.debop.redis.examples.springdata

import io.github.debop.kotlin.tests.containers.RedisContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
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

    companion object {
        val redisServer = RedisContainer.instance
    }

    @Autowired
    private lateinit var factory: RedisConnectionFactory

    @PreDestroy
    fun flushTestDb() {
        factory.connection.flushDb()
    }
}

fun main() {
    runApplication<SpringDataRedisApplication>()
}