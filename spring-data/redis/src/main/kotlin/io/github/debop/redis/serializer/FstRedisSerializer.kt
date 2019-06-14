package io.github.debop.redis.serializer

import mu.KLogging
import org.nustaq.serialization.FSTConfiguration
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.SerializationException

/**
 * Fst 라이브러리 for Java 7 or higher 를 이용한 ObjectSerializer
 *
 * 참고 : https://github.com/RuedigerMoeller/fast-serialization/wiki/Serialization
 *
 * @author debop
 * @since 19. 6. 14
 */
class FstRedisSerializer @JvmOverloads constructor(
    private val conf: FSTConfiguration = DefaultConfiguration) : RedisSerializer<Any> {

    companion object : KLogging() {
        val DefaultConfiguration: FSTConfiguration = FSTConfiguration.getDefaultConfiguration()

        val INSTANCE: FstRedisSerializer by lazy { FstRedisSerializer() }
    }

    override fun serialize(graph: Any?): ByteArray? {

        if (graph == null) {
            return ByteArray(0)
        }

        try {
            return conf.asByteArray(graph)
        } catch (e: Exception) {
            throw SerializationException("Cannot serialize", e)
        }
    }

    override fun deserialize(bytes: ByteArray?): Any? {
        if (bytes == null || bytes.isEmpty()) {
            return null
        }

        try {
            return conf.asObject(bytes)
        } catch (e: Exception) {
            throw SerializationException("Cannot deserialize", e)
        }
    }

}