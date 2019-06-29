package io.github.debop.jackson.binary.avro

import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord

/**
 * AvroGenericRecordSerializer
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
interface AvroGenericRecordSerializer {

    companion object {
        val emptyByteArray = ByteArray(0)
    }

    fun serialize(graph: GenericRecord?, schema: Schema): ByteArray

    fun deserialize(bytes: ByteArray?, schema: Schema): GenericRecord?

}