package io.github.debop.jackson.binary.avro

import org.apache.avro.specific.SpecificRecord

/**
 * Avro deserialize [bytes] to specific type [T]
 *
 * @param T     Type of deserialized instance
 * @param bytes Serialized data by Avro
 * @return deserialized instance or null
 */
inline fun <reified T: SpecificRecord> AvroSerializer.deserialize(bytes: ByteArray?): T? =
    deserialize(bytes, T::class.java)

/**
 * Avro deserialize [bytes] to collection of specific type [T]
 *
 * @param T     Type of deserialized instance
 * @param bytes Serialized data by Avro
 * @return      Collection of deserialized instance
 */
inline fun <reified T: SpecificRecord> AvroSerializer.deserializeList(bytes: ByteArray?): List<T> =
    deserializeList(bytes, T::class.java)
