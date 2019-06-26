package io.github.debop.redis.serializer

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.pool.KryoFactory
import com.esotericsoftware.kryo.pool.KryoPool
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer
import com.esotericsoftware.kryo.serializers.EnumNameSerializer
import de.javakaffee.kryoserializers.KryoReflectionFactorySupport
import mu.KLogging
import org.objenesis.strategy.StdInstantiatorStrategy

/**
 * KryoUtils
 *
 * @author debop
 * @since 19. 6. 14
 */
object KryoUtils : KLogging() {

    val factory: KryoFactory by lazy { KryoFactory { createKryo() } }

    val Pool: KryoPool by lazy {
        KryoPool.Builder(factory).softReferences().build()
    }

    fun createKryoReflectionFactorySupport(): Kryo = KryoReflectionFactorySupport()

    fun createKryo(): Kryo = Kryo().apply {
        logger.info { "Create new Kryo Instance..." }

        // no-arg constructor 가 없더라도 deserialize 가 가능하도록
        instantiatorStrategy = Kryo.DefaultInstantiatorStrategy(StdInstantiatorStrategy())

        // schema evolution 시 오류를 줄일 수 있다.
        setDefaultSerializer(CompatibleFieldSerializer::class.java)

        // enum ordinal 이 아닌 name 으로 직렬화
        addDefaultSerializer(Enum::class.java, EnumNameSerializer::class.java)

        register(java.util.Optional::class.java)
        register(java.time.Instant::class.java)
        register(java.time.LocalDate::class.java)
        register(java.time.LocalDateTime::class.java)
        register(java.time.LocalTime::class.java)
        register(java.time.OffsetTime::class.java)
        register(java.time.OffsetDateTime::class.java)
        register(java.time.ZonedDateTime::class.java)

    }

    /**
     * Kryo 를 이용한 작업을 함수로 표현
     * Kryo 가 thread-safe 하지 않기 때문에 이 함수를 사용해야 합니다.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> withKryo(func: Kryo.() -> T?): T? {
        val kryo = Pool.borrow()
        return try {
            func(kryo) // kryo.run(func)
        } catch (ignored: Exception) {
            logger.error(ignored) { "Fail to execute function by Kryo." }
            null
        } finally {
            Pool.release(kryo)
        }
    }
}