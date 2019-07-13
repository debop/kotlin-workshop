package io.github.debop.javers.mongodb

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.bson.Document
import org.bson.types.ObjectId
import java.math.BigDecimal

/**
 * DocumentConverter
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 12
 */
object DocumentConverter {

    fun toDocument(jsonObject: JsonObject): Document {
        val document = Document()

        jsonObject.entrySet().forEach { (key, jsonElem) ->
            document.append(key, fromJsonElement(jsonElem))
        }
        return document
    }

    fun fromDocument(document: Document): JsonElement {
        val jsonObject = JsonObject()
        document.entries.forEach { (key, value) ->
            jsonObject.add(key, createJsonElement(value))
        }
        return jsonObject
    }

    private fun fromJsonElement(jsonElem: JsonElement): Any? = when (jsonElem) {
        JsonNull.INSTANCE -> null
        is JsonObject -> toDocument(jsonElem)
        is JsonPrimitive -> {
            when {
                jsonElem.isString -> jsonElem.asString
                jsonElem.isNumber -> {
                    val value = jsonElem.asNumber
                    when (value) {
                        is BigDecimal -> {
                            try {
                                value.longValueExact()
                            } catch (e: ArithmeticException) {
                                value.toDouble()
                            }
                        }
                        else -> value
                    }
                }
                jsonElem.isBoolean -> jsonElem.asBoolean
                else ->
                    throw IllegalArgumentException("Unsupported JsonElement type - " + jsonElem.javaClass.simpleName)
            }
        }
        is JsonArray -> jsonElem.map { fromJsonElement(it) }

        else ->
            throw IllegalArgumentException("Unsupported JsonElement type - " + jsonElem.javaClass.simpleName)
    }

    private fun createJsonElement(dbObject: Any?): JsonElement = when (dbObject) {
        null -> JsonNull.INSTANCE
        is Document -> fromDocument(dbObject)
        is String -> JsonPrimitive(dbObject)
        is Number -> JsonPrimitive(dbObject)
        is Boolean -> JsonPrimitive(dbObject)
        is List<*> -> {
            val array = JsonArray()
            dbObject.forEach {
                array.add(createJsonElement(it))
            }
            array
        }
        is ObjectId -> {
            JsonObject().apply {
                addProperty("\$oid", dbObject.toString())
            }
        }

        else ->
            throw IllegalArgumentException("Unsupported dbObject type - " + dbObject.javaClass.simpleName)
    }
}