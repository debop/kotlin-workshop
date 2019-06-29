package io.github.debop.jackson.binary.avro

import io.github.debop.jackson.binary.avro.AvroSerializer.Companion.emptyByteArray
import mu.KLogging
import org.apache.avro.file.CodecFactory
import org.apache.avro.file.DataFileReader
import org.apache.avro.file.DataFileWriter
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.specific.SpecificRecord
import java.io.ByteArrayOutputStream


/**
 * Avro Protocol을 이용하여, Data 전송, RCP Call 을 수행할 수 있습니다.
 * 데이터 전송 시, [DefaultAvroSerializer]가 avro object 를 [ByteArray] 로 변환하고,
 * 수신하는 쪽에서 [ByteArray] 를 avro object로 빌드할 수 있습니다
 *
 * avro에서 제공하는 Encoder, Decoder는 avro object의 컬렉션을 유통할 때에는 shema 정보가 중복되므로 비효율적이다
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
class DefaultAvroSerializer @JvmOverloads constructor(
    private val codecFactory: CodecFactory = CodecFactory.snappyCodec()
): AvroSerializer {

    companion object: KLogging()

    override fun <T: SpecificRecord> serialize(graph: T?): ByteArray {
        if(graph == null) {
            return emptyByteArray
        }
        val datumWriter = SpecificDatumWriter<T>(graph.schema)
        DataFileWriter(datumWriter).setCodec(codecFactory).use { writer ->
            ByteArrayOutputStream().use { bos ->
                writer.create(graph.schema, bos)
                writer.append(graph)
                writer.flush()

                return bos.toByteArray()
            }
        }
    }

    override fun <T: SpecificRecord> serializeList(collection: Collection<T>?): ByteArray {
        if(collection.isNullOrEmpty()) {
            return emptyByteArray
        }

        val schema = collection.first().schema
        val datumWriter = SpecificDatumWriter<T>(schema)

        DataFileWriter(datumWriter).setCodec(codecFactory).use { writer ->
            ByteArrayOutputStream().use { bos ->
                writer.create(schema, bos)
                collection.forEach { writer.append(it) }
                writer.flush()

                return bos.toByteArray()
            }
        }
    }

    override fun <T: SpecificRecord> deserialize(bytes: ByteArray?, clazz: Class<T>): T? {
        if(bytes == null || bytes.isEmpty()) {
            return null
        }

        SeekableByteArrayInput(bytes).use { input ->
            val reader = SpecificDatumReader(clazz)
            DataFileReader(input, reader).use {
                return if(it.hasNext()) it.next() else null
            }
        }
    }

    override fun <T: SpecificRecord> deserializeList(bytes: ByteArray?, clazz: Class<T>): List<T> {
        if(bytes == null || bytes.isEmpty()) {
            return emptyList()
        }

        val result = mutableListOf<T>()
        SeekableByteArrayInput(bytes).use { input ->
            val reader = SpecificDatumReader(clazz)
            DataFileReader(input, reader).use {
                while(it.hasNext()) {
                    result += it.next()
                }
            }
        }
        return result
    }
}