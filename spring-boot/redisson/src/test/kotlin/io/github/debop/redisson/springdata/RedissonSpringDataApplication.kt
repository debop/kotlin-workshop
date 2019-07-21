package io.github.debop.redisson.springdata

import io.github.debop.kotlin.tests.containers.RedisServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import javax.annotation.PreDestroy

@EnableRedisRepositories
@SpringBootApplication
class RedissonSpringDataApplication {
    companion object {
        val redisServer = RedisServer()
    }

    @Autowired
    private lateinit var factory: RedisConnectionFactory

    @PreDestroy
    fun flushTestDb() {
        factory.connection.flushDb()
    }
}

fun main() {
    runApplication<RedissonSpringDataApplication>()
}