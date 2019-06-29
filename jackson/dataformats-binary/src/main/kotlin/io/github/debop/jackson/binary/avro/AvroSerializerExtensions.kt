package io.github.debop.jackson.binary.avro

import org.apache.avro.specific.SpecificRecord

/**
 * AvroSerializerExtensions
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */

inline fun <reified T: SpecificRecord> AvroSerializer.deserialize(bytes: ByteArray?): T? =
    deserialize(bytes, T::class.java)

inline fun <reified T: SpecificRecord> AvroSerializer.deserializeList(bytes: ByteArray?): List<T> =
    deserializeList(bytes, T::class.java)
