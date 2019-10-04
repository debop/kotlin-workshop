package io.github.debop.redis.examples.springdata.commands

import mu.KLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.util.UUID

/**
 * KeyOperationsTest
 *
 * @author debop
 * @since 19. 6. 14
 */
@SpringBootTest
class KeyOperationsTest {

    companion object: KLogging() {
        const val PREFIX = "KeyOperationsTest"
        const val KEY_PATTERN = PREFIX + "*"
    }

    @Autowired
    private lateinit var connectionFactory: RedisConnectionFactory

    private lateinit var connection: RedisConnection
    private val serializer = StringRedisSerializer()

    @BeforeEach
    fun setup() {
        connection = connectionFactory.connection
    }

    @Test
    fun `iterate over keys matching prefix using keys command`() {
        generateRandomKeys(1000)

        val keys = connection.keys(serializer.serialize(KEY_PATTERN))!!
        printKeys(keys.iterator())
    }

    // scan 으로 받으면 reactive 하게 처리할 수 있다
    @Test
    fun `iterate over keys matching prefix using scan command`() {
        generateRandomKeys(1000)

        val scanOption = ScanOptions.scanOptions().match(KEY_PATTERN).build()
        val cursor = connection.scan(scanOption)

        printKeys(cursor)
    }

    private fun generateRandomKeys(count: Int) {
        repeat(count) {
            connection.set((PREFIX + "-" + it).toByteArray(), UUID.randomUUID().toString().toByteArray())
        }
    }

    private fun printKeys(keys: Iterator<ByteArray>) {
        var i = 0
        keys.forEach {
            logger.debug { "key=${it.toString(Charsets.UTF_8)}" }
            i++
        }
        logger.debug { "Total No. found: $i" }
    }
}