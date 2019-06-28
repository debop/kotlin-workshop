package io.github.debop.jackson.binary.avro

/**
 * AvroSerializer
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 28
 */
interface AvroSerializer {

    fun <T: Any> serialize(graph: T?): ByteArray

    fun <T: Any> deserialize(bytes: ByteArray?, clazz: Class<T>): T?
}