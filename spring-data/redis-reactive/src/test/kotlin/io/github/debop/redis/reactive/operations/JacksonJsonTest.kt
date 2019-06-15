package io.github.debop.redis.reactive.operations

import io.github.debop.redis.reactive.RedisReactiveApplication
import io.github.debop.redis.reactive.domain.EmailAddress
import io.github.debop.redis.reactive.domain.Person
import io.github.debop.redis.toByteBuffer
import io.github.debop.redis.toUtf8String
import mu.KLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisOperations
import reactor.test.StepVerifier

/**
 * See: https://www.baeldung.com/jackson-annotations
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 14
 */
@SpringBootTest(classes = [RedisReactiveApplication::class])
class JacksonJsonTest {

    companion object: KLogging()

    @Autowired
    private lateinit var typedOperations: ReactiveRedisOperations<String, Person>

    @Autowired
    private lateinit var genericOperations: ReactiveRedisOperations<String, Any>

    /**
     * [ReactiveRedisOperations] using [String] keys and [Person] values serialized via
     * [org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer] to JSON without additional type
     * hints.
     *
     * @see RedisReactiveApplication#reactiveJsonPersonRedisTemplate(ReactiveRedisConnectionFactory)
     */
    @Test
    fun `write and read Person`() {
        StepVerifier.create(typedOperations.opsForValue().set("homer", Person("Homer", "Simpson")))
            .expectNext(true)
            .verifyComplete()

        val get = typedOperations
            .execute { it.stringCommands().get("homer".toByteBuffer()) }
            .map { it.toUtf8String() }
            .doOnNext { println(it) }

        StepVerifier.create(get)
            .expectNext("""
                {"_type":"io.github.debop.redis.reactive.domain.Person","firstname":"Homer","lastname":"Simpson"}
                """.trimIndent())
            .verifyComplete()

        StepVerifier.create(typedOperations.opsForValue().get("homer"))
            .expectNext(Person("Homer", "Simpson"))
            .verifyComplete()
    }

    /**
     * [ReactiveRedisOperations] using [String] keys and [Object] values serialized via
     * [org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer] to JSON without additional type
     * hints.
     *
     * @see RedisReactiveApplication#reactiveJsonPersonRedisTemplate(ReactiveRedisConnectionFactory)
     */
    @Test
    fun `write and read person object`() {
        StepVerifier.create(genericOperations.opsForValue().set("homer", Person("Homer", "Simpson")))
            .expectNext(true)
            .verifyComplete()

        val get = genericOperations
            .execute { it.stringCommands().get("homer".toByteBuffer()) }
            .map { it.toUtf8String() }
            .doOnEach { println(it) }

        StepVerifier.create(get)
            .expectNext("""
                {"_type":"io.github.debop.redis.reactive.domain.Person","firstname":"Homer","lastname":"Simpson"}
                """.trimIndent())
            .verifyComplete()

        StepVerifier.create(genericOperations.opsForValue().get("homer"))
            .expectNext(Person("Homer", "Simpson"))
            .verifyComplete()
    }

    /**
     * [ReactiveRedisOperations] using [String] keys and [Object] values serialized via
     * [org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer] to JSON without additional type
     * hints.
     *
     * @see RedisReactiveApplication#reactiveJsonPersonRedisTemplate(ReactiveRedisConnectionFactory)
     */
    @Test
    fun `write and read email object`() {
        StepVerifier.create(genericOperations.opsForValue().set("email", EmailAddress("homer@the-simpson.com")))
            .expectNext(true)
            .verifyComplete()

        val get = genericOperations
            .execute { it.stringCommands().get("email".toByteBuffer()) }
            .map { it.toUtf8String() }
            .doOnNext { println(it) }

        StepVerifier.create(get)
            .expectNext("""
                {"_type":"io.github.debop.redis.reactive.domain.EmailAddress","address":"homer@the-simpson.com"}
                """.trimIndent())
            .verifyComplete()

        StepVerifier.create(genericOperations.opsForValue().get("email"))
            .expectNext(EmailAddress("homer@the-simpson.com"))
            .verifyComplete()
    }
}