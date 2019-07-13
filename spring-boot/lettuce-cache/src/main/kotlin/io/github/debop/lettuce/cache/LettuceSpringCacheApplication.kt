package io.github.debop.lettuce.cache

import io.github.debop.kotlin.tests.containers.RedisServer
import mu.KLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * LettuceSpringCacheApplication
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 14
 */
@EnableScheduling
@SpringBootApplication
class LettuceSpringCacheApplication {

    companion object: KLogging() {
        val redisServer = RedisServer()
    }
}


fun main() {
    SpringApplicationBuilder()
        .sources(LettuceSpringCacheApplication::class.java)
        .profiles("app")
        .run()
}