package io.github.debop.redis.reactive.operations

import io.github.debop.redis.reactive.RedisReactiveApplication
import mu.KLogging
import org.amshove.kluent.shouldBeLessThan
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisOperations
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.test.StepVerifier
import java.time.Duration

@SpringBootTest(classes = [RedisReactiveApplication::class])
class ValueOperationTest {

    companion object: KLogging()

    @Autowired
    private lateinit var operations: ReactiveRedisOperations<String, String>

    @BeforeEach
    fun before() {
        val command = operations.execute { it.serverCommands().flushDb() }
        StepVerifier.create(command)
            .expectNext("OK")
            .verifyComplete()
    }

    @Test
    fun `should cache value`() {
        val cacheKey = "cacheKey"
        val valueOps = operations.opsForValue()

        val cacheMono = valueOps.get(cacheKey)
            .switchIfEmpty {
                cacheValue()
                    .flatMap {
                        valueOps.set(cacheKey, it, Duration.ofSeconds(60)).then(Mono.just(it))
                    }
            }

        logger.info { "Initial access (takes a while...)" }

        StepVerifier.create(cacheMono).expectSubscription()
            .expectNoEvent(Duration.ofSeconds(9))
            .expectNext("Hello, World!")
            .verifyComplete()

        logger.info { "Subsequent access (use cached value)" }

        val duration = StepVerifier.create(cacheMono)
            .expectNext("Hello, World!")
            .verifyComplete()

        logger.info { "Done" }

        duration.seconds shouldBeLessThan 2
    }

    private fun cacheValue(): Mono<String> {
        return Mono.delay(Duration.ofSeconds(10))
            .then(Mono.just("Hello, World!"))
    }
}