package io.github.debop.redis.example.redisson

import io.github.debop.kotlin.tests.containers.RedisContainer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

/**
 * RedissonApplication
 * @author debop (Sunghyouk Bae)
 */
@SpringBootApplication
@EnableCaching
class RedissonApplication {

}

fun main() {
    val redisServer = RedisContainer.instance

    runApplication<RedissonApplication>()
}