package io.github.debop.redis.reactive

import io.github.debop.kotlin.tests.containers.RedisServer
import io.github.debop.redis.reactive.domain.Person
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.WebApplicationType.REACTIVE
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import javax.annotation.PreDestroy

@SpringBootApplication
class RedisReactiveApplication {

    companion object: KLogging() {
        // 테스트용 Redis Server by Docker
        val redisServer = RedisServer()
    }

    @Value("\${spring.redis.host}")
    var host: String = "localhost"

    @Value("\${spring.redis.port}")
    var port: Int = 6379

    @Autowired
    lateinit var factory: RedisConnectionFactory

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val configuration = RedisStandaloneConfiguration(host, port)
        logger.debug { "Redis configuration=$configuration" }

        return LettuceConnectionFactory(configuration)
    }

    @Bean
    fun reactiveRedisTemplate(connectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {
        return ReactiveRedisTemplate<String, String>(connectionFactory,
                                                     RedisSerializationContext.string())
    }

    @Bean
    fun reactiveJsonPersonRedisTemplate(connectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Person> {
        val serializer = Jackson2JsonRedisSerializer(Person::class.java)

        val serializationContext =
            RedisSerializationContext
                .newSerializationContext<String, Person>(StringRedisSerializer())
                .value(serializer)
                .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }

    @Bean
    fun reactiveJsonObjectRedisTemplate(connectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, Any> {
        // class 정보를 _type 에 포함시킨다
        // Object Mapper 에 아래와 같이 설정하도록 되어 있다
        /*
            // simply setting {@code mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)} does not help here since we need
            // the type hint embedded for deserialization using the default typing feature.
            mapper.registerModule(new SimpleModule().addSerializer(new NullValueSerializer(classPropertyTypeName)));

            if (StringUtils.hasText(classPropertyTypeName)) {
                mapper.enableDefaultTypingAsProperty(DefaultTyping.NON_FINAL, classPropertyTypeName);
            } else {
                mapper.enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY);
            }
         */
        val serializer = GenericJackson2JsonRedisSerializer("_type")

        val serializationContext =
            RedisSerializationContext
                .newSerializationContext<String, Any>(StringRedisSerializer())
                .value(serializer)
                .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }

    @PreDestroy
    fun flushTestDb() {
        runCatching { factory.connection.flushDb() }
    }
}

fun main() {
    runApplication<RedisReactiveApplication>() {
        webApplicationType = REACTIVE
    }
}