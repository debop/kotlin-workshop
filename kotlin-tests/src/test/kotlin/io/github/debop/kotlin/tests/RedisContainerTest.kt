package io.github.debop.kotlin.tests

import io.lettuce.core.LettuceFutures
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisFuture
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.reactive.RedisReactiveCommands
import io.lettuce.core.api.sync.RedisCommands
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.Duration

class RedisContainerTest {

    lateinit var redisServer: RedisContainer
    lateinit var redisClient: RedisClient

    @BeforeAll
    fun `setup all`() {
        redisServer = RedisContainer.instance
        redisClient = RedisClient.create(redisServer.url)
    }

    @AfterAll
    fun `cleanup all`() {
        if (this::redisClient.isInitialized) {
            redisClient.shutdown()
        }
        if (this::redisServer.isInitialized) {
            redisServer.close()
        }
    }

    private fun withCommands(block: RedisCommands<String, String>.() -> Unit) {
        redisClient.connect().use {
            block.invoke(it.sync())
        }
    }

    private fun withAsyncCommands(block: RedisAsyncCommands<String, String>.() -> Unit) {
        val connection = redisClient.connect()
        try {
            block.invoke(connection.async())
        } finally {
            connection.closeAsync().get()
        }
    }

    private fun withReactiveCommands(block: RedisReactiveCommands<String, String>.() -> Unit) {
        redisClient.connect().use {
            block.invoke(it.reactive())
        }
    }

    private fun <T> withBatch(duration: Duration = Duration.ofSeconds(30),
                              block: RedisAsyncCommands<String, String>.() -> List<RedisFuture<T>>): List<T> {
        redisClient.connect().use {
            val commands = it.async()
            commands.setAutoFlushCommands(false)

            val futures = block.invoke(commands)

            commands.flushCommands()
            LettuceFutures.awaitAll(duration, *futures.toTypedArray())
            return futures.map { future -> future.get() }
        }
    }

    @Test
    fun `connect to redis server`() {
        withCommands {
            set("sync-key", "sync-value")
            val actual = get("sync-key")
            actual shouldEqual "sync-value"
        }
    }

    @Test
    fun `using asynchronous command`() {
        withAsyncCommands {
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
        withReactiveCommands {
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

        val results = withBatch(Duration.ofMinutes(1)) {

            List(batchSize) { index ->
                set("batch-key-$index", "batch-value-$index")
            }
                .toList()
        }

        results.size shouldEqual batchSize
    }
}