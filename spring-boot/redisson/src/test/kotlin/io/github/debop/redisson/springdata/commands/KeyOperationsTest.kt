package io.github.debop.redisson.springdata.commands

import mu.KLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.UUID

/**
 * KeyOperationsTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 13
 */
@ExtendWith(SpringExtension::class)
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
            println("key=${it.toString(Charsets.UTF_8)}")
            i++
        }
        println("Total No. found: $i")
    }

}