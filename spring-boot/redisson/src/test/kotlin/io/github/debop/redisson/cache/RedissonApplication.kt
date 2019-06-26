package io.github.debop.redisson.cache

import io.github.debop.kotlin.tests.containers.RedisContainer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class RedissonApplication {
    companion object {
        val redisServer = RedisContainer.Instance
    }
}

fun main() {
    runApplication<RedissonApplication>()
}