package io.github.debop.redis.reactive.operations

import io.github.debop.redis.reactive.RedisReactiveApplication
import mu.KLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisOperations
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.logging.Level

/**
 * ListOperationTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 15
 */
@SpringBootTest(classes = [RedisReactiveApplication::class])
class ListOperationTest {

    companion object: KLogging()

    @Autowired
    private lateinit var operations: ReactiveRedisOperations<String, String>

    /**
     * A simple queue using Redis blocking list commands [BLPOP] and [LPUSH] to produce the queue message.
     */
    @Test
    fun `pool and populate queue`() {
        val queue = "queue"
        val listOperations = operations.opsForList()

        val blpop = listOperations
            .leftPop(queue, Duration.ofSeconds(30))
            .log("io.github.debop.redis.reactive", Level.INFO)

        logger.info { "Blocking pop ... waiting for message" }

        StepVerifier.create(blpop)
            .then {
                Mono.delay(Duration.ofSeconds(1))
                    .doOnSuccess { logger.info { "Subscriber produces message" } }
                    .then(listOperations.leftPush(queue, "Hello, World!"))
                    .subscribe()
            }
            .expectNext("Hello, World!")
            .verifyComplete()

        logger.info { "Blocking pop... done!" }
    }
}