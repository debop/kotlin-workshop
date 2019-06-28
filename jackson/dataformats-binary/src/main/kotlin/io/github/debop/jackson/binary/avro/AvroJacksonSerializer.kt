package io.github.debop.jackson.binary.avro

import com.fasterxml.jackson.databind.util.LRUMap
import com.fasterxml.jackson.dataformat.avro.AvroMapper
import com.fasterxml.jackson.dataformat.avro.AvroSchema
import com.fasterxml.jackson.dataformat.avro.schema.AvroSchemaGenerator
import mu.KLogging

/**
 * AvroJacksonSerializer
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 28
 */
class AvroJacksonSerializer(private val mapper: AvroMapper): AvroSerializer {

    companion object: KLogging() {
        private val schemas = LRUMap<Class<*>, AvroSchema>(10, 100)
    }

    override fun <T: Any> serialize(graph: T?): ByteArray {
        if(graph == null) {
            return ByteArray(0)
        }
        val schema = getSchema(graph.javaClass)
        return mapper.writer(schema).writeValueAsBytes(graph)
    }


    override fun <T: Any> deserialize(bytes: ByteArray?, clazz: Class<T>): T? {
        if(bytes == null || bytes.isEmpty()) {
            return null
        }
        val schema = getSchema(clazz)
        return mapper.readerFor(clazz).with(schema).readValue(bytes)
    }

    private fun getSchema(clazz: Class<*>): AvroSchema {
        val schema = schemas[clazz]
        if(schema != null) {
            return schema
        }

        val schemaGenerator = AvroSchemaGenerator()
        mapper.acceptJsonFormatVisitor(clazz, schemaGenerator)
        schemas.putIfAbsent(clazz, schemaGenerator.generatedSchema)
        return schemas[clazz]
    }
}