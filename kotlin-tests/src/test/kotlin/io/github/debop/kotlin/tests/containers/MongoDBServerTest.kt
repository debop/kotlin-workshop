package io.github.debop.kotlin.tests.containers

import com.mongodb.client.MongoClients
import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.bson.Document
import org.junit.jupiter.api.Test

class MongoDBServerTest {

    companion object: KLogging() {
        val mongodb: MongoDBServer = MongoDBServer()
    }

    val client = MongoClients.create(mongodb.connectionString)

    @Test
    fun `create mongodb testcontainer instance`() {
        mongodb.shouldNotBeNull()
        logger.debug { "MongoDB host=${mongodb.host}, port=${mongodb.port}" }
    }

    @Test
    fun `connect to mongodb`() {
        client.listDatabaseNames().forEach { database ->
            logger.debug { "Database name=$database" }

            val db = client.getDatabase(database)
            db.listCollectionNames().forEach { collection ->
                logger.debug { "  Collection=$collection" }
            }
        }
    }

    @Test
    fun `save and read with customer collection`() {
        val db = client.getDatabase("local")

        db.createCollection("customers")
        val customers = db.getCollection("customers")

        val document = Document().apply {
            put("name", "Debop")
            put("company", "Coupang")
        }
        customers.insertOne(document)

        val loaded = customers.find().toList()
        loaded.size shouldBeEqualTo 1
    }
}