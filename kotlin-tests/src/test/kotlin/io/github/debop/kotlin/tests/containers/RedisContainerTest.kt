package io.github.debop.kotlin.tests.containers

import io.lettuce.core.LettuceFutures
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisFuture
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.reactive.RedisReactiveCommands
import io.lettuce.core.api.sync.RedisCommands
import mu.KLogging
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.Duration

class RedisContainerTest {

    companion object: KLogging() {
        val redisServer: RedisContainer = RedisContainer.instance

        private fun RedisClient.withCommands(block: RedisCommands<String, String>.() -> Unit) {
            connect().use {
                block.invoke(it.sync())
            }
        }

        private fun RedisClient.withAsyncCommands(block: RedisAsyncCommands<String, String>.() -> Unit) {
            val connection = connect()
            try {
                block.invoke(connection.async())
            } finally {
                connection.closeAsync().get()
            }
        }

        private fun RedisClient.withReactiveCommands(block: RedisReactiveCommands<String, String>.() -> Unit) {
            connect().use {
                block.invoke(it.reactive())
            }
        }

        private fun <T> RedisClient.withBatch(timeout: Duration = Duration.ofSeconds(30),
                                              block: RedisAsyncCommands<String, String>.() -> List<RedisFuture<T>>): List<T> {
            connect().use {
                val commands = it.async()
                commands.setAutoFlushCommands(false)

                val futures = block.invoke(commands)

                commands.flushCommands()
                LettuceFutures.awaitAll(timeout, *futures.toTypedArray())
                return futures.map { future -> future.get() }
            }
        }
    }


    lateinit var redisClient: RedisClient

    @BeforeAll
    fun `setup all`() {
        redisClient = RedisClient.create(redisServer.url)
    }

    @AfterAll
    fun `cleanup all`() {
        if(this::redisClient.isInitialized) {
            redisClient.shutdown()
        }
        redisServer.close()
    }


    @Test
    fun `connect to redis server`() {
        redisClient.withCommands {
            set("sync-key", "sync-value")
            val actual = get("sync-key")
            actual shouldEqual "sync-value"
        }
    }

    @Test
    fun `using asynchronous command`() {
        redisClient.withAsyncCommands {
            set("async-key", "async-value").get()

            get("async-key").whenComplete { v, e ->
                v shouldEqual "async-value"
                e.shouldBeNull()
            }
                .toCompletableFuture()
                .join()
        }
    }

    @Test
    fun `using reactive command`() {
        redisClient.withReactiveCommands {
            set("reactive-key", "reactive-value").subscribe()

            get("reactive-key")
                .subscribe {
                    it shouldEqual "reactive-value"
                }
        }
    }

    @Test
    fun `execute in batch`() {
        val batchSize = 100

        val results = redisClient.withBatch(Duration.ofMinutes(1)) {

            List(batchSize) { index ->
                set("batch-key-$index", "batch-value-$index")
            }
                .toList()
        }

        results.size shouldEqual batchSize
    }
}