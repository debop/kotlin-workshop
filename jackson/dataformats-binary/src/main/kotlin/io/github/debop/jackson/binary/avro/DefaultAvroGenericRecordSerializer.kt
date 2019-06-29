package io.github.debop.jackson.binary.avro

import io.github.debop.jackson.binary.avro.AvroGenericRecordSerializer.Companion.emptyByteArray
import org.apache.avro.Schema
import org.apache.avro.file.CodecFactory
import org.apache.avro.file.DataFileReader
import org.apache.avro.file.DataFileWriter
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import java.io.ByteArrayOutputStream

/**
 * Avro [GenericRecord]에 대해 serialize/deserialize를 수행합니다.
 * NOTE: 단 `enum` 에 대해서는 deserialize를 하지 못하는 제약이 있습니다.
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
class DefaultAvroGenericRecordSerializer @JvmOverloads constructor(
    private val codecFactory: CodecFactory = CodecFactory.snappyCodec()
): AvroGenericRecordSerializer {

    override fun serialize(graph: GenericRecord?, schema: Schema): ByteArray {
        if(graph == null) {
            return emptyByteArray
        }

        val datumWriter = GenericDatumWriter<GenericRecord>(schema)
        DataFileWriter<GenericRecord>(datumWriter).setCodec(codecFactory).use { writer ->
            ByteArrayOutputStream().use { bos ->
                writer.create(schema, bos)
                writer.append(graph)
                writer.flush()
                return bos.toByteArray()
            }
        }
    }

    override fun deserialize(bytes: ByteArray?, schema: Schema): GenericRecord? {
        if(bytes == null || bytes.isEmpty()) {
            return null
        }
        SeekableByteArrayInput(bytes).use { input ->
            val datumReader = GenericDatumReader<GenericRecord>(schema)
            DataFileReader<GenericRecord>(input, datumReader).use { reader ->
                return if(reader.hasNext()) reader.next() else null
            }
        }
    }
}