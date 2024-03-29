package io.github.debop.redisson.cache

import java.io.Serializable
import java.util.concurrent.TimeUnit
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.redisson.api.RedissonClient
import org.redisson.api.map.event.EntryCreatedListener
import org.redisson.api.map.event.EntryExpiredListener
import org.redisson.client.codec.StringCodec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [RedissonApplication::class],
    // application.properties 에서 읽거나, 이렇게 재정의하면 됩니다.
                properties = [
                    "spring.redis.host=\${testcontainers.redis.host}",
                    "spring.redis.port=\${testcontainers.redis.port}",
                    "spring.redis.url=\${testcontainers.redis.url}"
                ])
class RedissonAutoConfigurationTests {

    companion object: KLogging()

    @Autowired
    private lateinit var redisson: RedissonClient

    @Autowired
    private lateinit var template: StringRedisTemplate

    @Test
    fun `context loading`() {
        redisson.shouldNotBeNull()
        template.shouldNotBeNull()
    }

    @Test
    fun `connect to redis server with redisson`() {
        redisson.keys.flushall()

        val map = redisson.getMap<String, String>("test", StringCodec.INSTANCE)
        map["1"] = "2"

        val hash = template.boundHashOps<String, String>("test")
        val saved = hash["1"]
        saved shouldBeEqualTo "2"
    }

    data class Item(val id: Long, val name: String): Serializable

    val createdListener = EntryCreatedListener<Long, Item> { evt ->
        logger.trace { "Cache item created. id=${evt.key}" }
    }
    val expiredListener = EntryExpiredListener<Long, Item> { evt ->
        logger.trace { "Cache item expired. id=${evt.key}" }
    }

    @Test
    fun `save reference object`() {

        val cache = redisson.getMapCache<Long, Item>("cache:item")
        cache.addListener(createdListener)
        cache.addListener(expiredListener)

        val item = Item(123, "debop")

        cache.fastPut(item.id, item, 1000, TimeUnit.MILLISECONDS) shouldBeEqualTo true

        val cached = cache[item.id]
        cached.shouldNotBeNull()
        cached shouldBeEqualTo item

        cache.remainTimeToLive(item.id) shouldBeGreaterThan 0

        // expiry가 1초 이므로, 그 이상의 시간을 보내면 key는 expire 된다.
        Thread.sleep(1300)

        cache[item.id].shouldBeNull()
        cache.remainTimeToLive(item.id) shouldBeEqualTo -2  // key does not exists
    }

}
