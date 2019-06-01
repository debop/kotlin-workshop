package io.github.debop.kotlin.tests.containers

import com.mongodb.MongoClient
import mu.KLogging
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.bson.Document
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class MongoDBContainerTest {

    companion object : KLogging()

    lateinit var mongodb: MongoDBContainer

    @BeforeAll
    fun `setup all`() {
        mongodb = MongoDBContainer.instance
    }

    @AfterAll
    fun `cleanup all`() {
        if (this::mongodb.isInitialized) {
            mongodb.close()
        }
    }

    @Test
    fun `create mongodb testcontainer instance`() {
        mongodb.shouldNotBeNull()
        logger.debug { "MongoDB host=${mongodb.host}, port=${mongodb.port}" }
    }

    @Test
    fun `connect to mongodb`() {
        val client = MongoClient(mongodb.host, mongodb.port)

        client.listDatabaseNames().forEach {
            logger.debug { "Database name=$it" }
        }

        val db = client.getDatabase("local")
        db.listCollectionNames().forEach {
            logger.debug { "  Collection=$it" }
        }
    }

    @Test
    fun `save and read with customer collection`() {
        val client = MongoClient(mongodb.host, mongodb.port)

        val db = client.getDatabase("local")

        db.createCollection("customers")
        val customers = db.getCollection("customers")

        val document = Document().apply {
            put("name", "Debop")
            put("company", "Coupang")
        }
        customers.insertOne(document)

        val loaded = customers.find().toList()
        loaded.size shouldEqualTo 1
    }
}