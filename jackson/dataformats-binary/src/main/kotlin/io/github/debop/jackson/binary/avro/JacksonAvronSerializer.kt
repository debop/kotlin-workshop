package io.github.debop.jackson.binary.avro

import com.fasterxml.jackson.databind.util.LRUMap
import com.fasterxml.jackson.dataformat.avro.AvroMapper
import com.fasterxml.jackson.dataformat.avro.AvroSchema
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.debop.jackson.binary.avro.AvroSerializer.Companion.emptyByteArray
import mu.KLogging
import org.apache.avro.specific.SpecificRecord
import java.io.ByteArrayOutputStream

/**
 * jackson-dataformat-avro 를 이용하여, Schema 정보를 포함하지 않고 객체를 직렬화 합니다.
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 28
 */
class JacksonAvronSerializer(private val mapper: AvroMapper = defaultAvroMapper): AvroSerializer {

    companion object: KLogging() {

        val defaultAvroMapper = AvroMapper().apply { registerKotlinModule() }

        /**
         * 특정 수형에 대한 Schema 를 Cache 하도록 합니다.
         */
        private val schemaCache = LRUMap<Class<*>, AvroSchema>(10, 100)

        private fun AvroMapper.getSchema(clazz: Class<*>): AvroSchema {
            val schema = schemaCache[clazz]
            if(schema != null) {
                return schema
            }

            val schemaGenerator = AvroSchemaGenerator()
            this.acceptJsonFormatVisitor(clazz, schemaGenerator)
            schemaCache.putIfAbsent(clazz, schemaGenerator.generatedSchema)
            return schemaCache[clazz]
        }
    }

    override fun <T: SpecificRecord> serialize(graph: T?): ByteArray {
        if(graph == null) {
            return emptyByteArray
        }
        val schema = mapper.getSchema(graph.javaClass)
        return mapper.writer(schema).writeValueAsBytes(graph)
    }

    override fun <T: SpecificRecord> serializeList(collection: Collection<T>?): ByteArray {
        if(collection.isNullOrEmpty()) {
            return emptyByteArray
        }
        val schema = mapper.getSchema(collection.first().javaClass)

        ByteArrayOutputStream().use { bos ->
            mapper.writer(schema).writeValues(bos).writeAll(collection)
            return bos.toByteArray()
        }
    }

    override fun <T: SpecificRecord> deserialize(bytes: ByteArray?, clazz: Class<T>): T? {
        if(bytes == null || bytes.isEmpty()) {
            return null
        }
        val schema = mapper.getSchema(clazz)
        return mapper.readerFor(clazz).with(schema).readValue(bytes)
    }

    override fun <T: SpecificRecord> deserializeList(bytes: ByteArray?, clazz: Class<T>): List<T> {
        if(bytes == null || bytes.isEmpty()) {
            return emptyList()
        }

        val schema = mapper.getSchema(clazz)

        return mapper.readerFor(clazz).with(schema).readValues<T>(bytes).asSequence().toList()
    }
}