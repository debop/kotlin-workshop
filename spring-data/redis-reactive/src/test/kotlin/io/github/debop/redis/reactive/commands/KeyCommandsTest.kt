package io.github.debop.redis.reactive.commands

import io.github.debop.redis.reactive.RedisReactiveApplication
import io.github.debop.redis.toByteBuffer
import io.github.debop.redis.toUtf8String
import mu.KLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.ReactiveRedisConnection
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.ReactiveStringCommands
import org.springframework.data.redis.serializer.StringRedisSerializer
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.nio.ByteBuffer
import java.time.Duration
import java.util.UUID

/**
 * KeyCommandsTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 14
 */
@SpringBootTest(classes = [RedisReactiveApplication::class])
class KeyCommandsTest {

    companion object: KLogging() {
        val PREFIX: String = KeyCommandsTest::class.java.simpleName
        val KEY_PATTERN = "$PREFIX*"

        const val KEY_COUNT: Long = 100L
    }

    @Autowired
    private lateinit var connectionFactory: ReactiveRedisConnectionFactory

    private lateinit var connection: ReactiveRedisConnection
    private val serializer = StringRedisSerializer.UTF_8

    @BeforeEach
    fun setup() {
        connection = connectionFactory.reactiveConnection
    }

    @Test
    fun `iterate over keys matching prefix using keys command`() {

        generateRandomKeys(KEY_COUNT)

        val keyCount = connection.keyCommands()
            .keys(ByteBuffer.wrap(serializer.serialize(KEY_PATTERN)))
            .flatMapMany { Flux.fromIterable(it) }
            .doOnNext { println(it.toUtf8String()) }
            .count()
            .doOnSuccess { println("Total No. found: $it") }

        StepVerifier
            .create(keyCount)
            .expectNext(KEY_COUNT)
            .verifyComplete()
    }

    @Test
    fun `store by RPUSH and read BRPOP`() {
        val popResult = connection.listCommands()
            .brPop(listOf("list".toByteBuffer()), Duration.ofSeconds(5))

        val llen = connection.listCommands().lLen("list".toByteBuffer())

        val popAndLlen = connection.listCommands()
            .rPush("list".toByteBuffer(), listOf("item".toByteBuffer()))
            .flatMap { popResult }
            .doOnNext { println(it.value.toUtf8String()) }
            .flatMap { llen }
            .doOnNext { count -> println("Total items in list left: $count") }

        StepVerifier
            .create(popAndLlen)
            .expectNext(0L)
            .verifyComplete()
    }

    @Suppress("SameParameterValue")
    private fun generateRandomKeys(count: Long = 100L) {
        val keyFlux = Flux.range(0, count.toInt()).map { "$PREFIX-$it" }
        val generator = keyFlux
            .map { it.toByteBuffer() }
            .map { key ->
                ReactiveStringCommands.SetCommand
                    .set(key)
                    .value(ByteBuffer.wrap(UUID.randomUUID().toString().toByteArray()))
            }

        StepVerifier
            .create(connection.stringCommands().set(generator))
            .expectNextCount(count)
            .verifyComplete()
    }

}