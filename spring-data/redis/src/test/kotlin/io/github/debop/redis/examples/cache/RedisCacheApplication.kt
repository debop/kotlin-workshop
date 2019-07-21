package io.github.debop.redis.examples.cache

import io.github.debop.kotlin.tests.containers.RedisServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

/**
 * RedisCacheApplication
 *
 * @author debop
 * @since 19. 6. 14
 */

@EnableCaching
@SpringBootApplication
class RedisCacheApplication {

    companion object {
        // TestContainers를 이용한 Docker Instance
        val redisServer = RedisServer()
    }
}

fun main() {
    runApplication<RedisCacheApplication>()
}