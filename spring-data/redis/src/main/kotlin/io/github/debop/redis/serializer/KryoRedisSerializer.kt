package io.github.debop.redis.serializer

import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import mu.KLogging
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.SerializationException
import java.io.ByteArrayOutputStream

/**
 * Kryo 를 이용한 ObjectSerializer
 * Thread-safe 를 지원하면 Size 가 큰 DTO의 경우 Binary Serializer 보다 더 느린 경우가 있다
 * 물론 Thread-safe 하지 않으면 Binary Serializer 보다 빠르다 (단 사용할 수 없다)
 *
 * @author debop
 * @since 19. 6. 14
 */
class KryoRedisSerializer : RedisSerializer<Any> {

    companion object : KLogging() {
        val INSTANCE: KryoRedisSerializer by lazy { KryoRedisSerializer() }
    }

    override fun serialize(graph: Any?): ByteArray? {
        if (graph == null) {
            return ByteArray(0)
        }

        return try {
            ByteArrayOutputStream().use { bos ->
                Output(bos).use { output ->
                    KryoUtils.withKryo { writeClassAndObject(output, graph) }
                    output.flush()
                    bos.toByteArray()
                }
            }
        } catch (e: Exception) {
            throw SerializationException("Cannot serialize", e)
        }
    }

    override fun deserialize(bytes: ByteArray?): Any? {
        if (bytes == null || bytes.isEmpty()) {
            return null
        }

        return try {
            Input(bytes).use { input ->
                KryoUtils.withKryo {
                    readClassAndObject(input)
                }
            }
        } catch (e: Exception) {
            throw SerializationException("Cannot deserialize", e)
        }
    }
}