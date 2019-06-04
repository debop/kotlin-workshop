package io.github.debop.redis.example

import io.github.debop.kotlin.tests.containers.RedisContainer
import mu.KotlinLogging.logger

/**
 * AbstractRedisTests
 * @author debop (Sunghyouk Bae)
 */
abstract class AbstractRedisTests {

    companion object {

        val log = logger {}

        val redisServer = RedisContainer.instance
    }
}