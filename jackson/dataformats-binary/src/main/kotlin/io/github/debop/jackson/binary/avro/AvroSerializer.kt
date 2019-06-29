package io.github.debop.jackson.binary.avro

import org.apache.avro.specific.SpecificRecord

/**
 * AvroSerializer
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 28
 */
interface AvroSerializer {

    companion object {
        val emptyByteArray = ByteArray(0)
    }

    fun <T: SpecificRecord> serialize(graph: T?): ByteArray

    fun <T: SpecificRecord> serializeList(collection: Collection<T>?): ByteArray

    fun <T: SpecificRecord> deserialize(bytes: ByteArray?, clazz: Class<T>): T?

    fun <T: SpecificRecord> deserializeList(bytes: ByteArray?, clazz: Class<T>): List<T>
}