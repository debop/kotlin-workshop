package io.github.debop.jackson.binary.avro

import io.github.debop.jackson.binary.avro.AvroSerializer.Companion.emptyByteArray
import org.apache.avro.specific.SpecificRecord
import org.xerial.snappy.Snappy

/**
 * Decorator class for [JacksonAvronSerializer]
 */
abstract class AvroJacksonSerializerDecorator(
    private val serializer: JacksonAvronSerializer = JacksonAvronSerializer()
): AvroSerializer by serializer

/**
 * [JacksonAvronSerializer] 를 이용하여 직렬화를 수행한 것을 Snappy 를 이용하여 압축을 수행합니다.
 */
class SnappyAvroJacksonSerializer: AvroJacksonSerializerDecorator(JacksonAvronSerializer()) {

    override fun <T: SpecificRecord> serialize(graph: T?): ByteArray {
        val bytes = super.serialize(graph)
        return if(bytes.isNotEmpty()) Snappy.compress(bytes) else emptyByteArray
    }

    override fun <T: SpecificRecord> deserialize(bytes: ByteArray?, clazz: Class<T>): T? {
        if(bytes == null || bytes.isEmpty()) {
            return null
        }
        return super.deserialize(Snappy.uncompress(bytes), clazz)
    }
}