package io.github.debop.javers.mongodb

import com.mongodb.BasicDBObject
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import io.github.debop.javers.mongodb.MongoDBDialect.DOCUMENT_DB
import io.github.debop.javers.mongodb.MongoDBDialect.MONGO_DB
import io.github.debop.javers.mongodb.model.MongoDBHeadId
import mu.KLogging
import org.bson.Document

/**
 * MongoDBSchemaManager
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 12
 */
class MongoDBSchemaManager(private val mongo: MongoDatabase) {

    companion object: KLogging() {
        const val ASC = 1
        const val SNAPSHOTS = "cv_snapshots"

        private const val COMMIT_METADATA = "commitMetadata"

        const val COMMIT_ID = "$COMMIT_METADATA.id"
        const val COMMIT_DATE = "$COMMIT_METADATA.commitDate"
        const val COMMIT_DATE_INSTANT = "$COMMIT_METADATA.commitDateInstant"
        const val COMMIT_AUTHOR = "$COMMIT_METADATA.author"
        const val COMMIT_PROPERTIES = "$COMMIT_METADATA.properties"
        const val COMMIT_PROPERTIES_INDEX_NAME = "$COMMIT_METADATA.properties_key_value"

        const val GLOBAL_ID = "globalId"
        const val GLOBAL_ID_KEY = GLOBAL_ID + "_key"
        const val GLOBAL_ID_ENTITY = "$GLOBAL_ID.entity"
        const val GLOBAL_ID_OWNER_ID_ENTITY = "$GLOBAL_ID.ownerId.entity"
        const val GLOBAL_ID_FRAGMENT = "$GLOBAL_ID.fragment"
        const val GLOBAL_ID_VALUE_OBJECT = "$GLOBAL_ID.valueObject"

        const val SNAPSHOT_VERSION = "version"
        const val CHANGED_PROPERTIES = "changedProperties"
        const val OBJECT_ID = "_id"
        const val SNAPSHOT_TYPE = "type"
    }

    fun ensureSchema(dialect: MongoDBDialect) {
        // ensures collections and indexes
        val snapshots = snapshotsCollection()
        snapshots.createIndex(BasicDBObject(GLOBAL_ID_KEY, ASC))
        snapshots.createIndex(BasicDBObject(GLOBAL_ID_VALUE_OBJECT, ASC))
        snapshots.createIndex(BasicDBObject(GLOBAL_ID_ENTITY, ASC))
        snapshots.createIndex(BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ASC))
        snapshots.createIndex(BasicDBObject(CHANGED_PROPERTIES, ASC))

        when (dialect) {
            MONGO_DB -> {
                snapshots.createIndex(BasicDBObject("$COMMIT_PROPERTIES.key", ASC)
                                          .append("$COMMIT_PROPERTIES.value", ASC),
                                      IndexOptions().name(COMMIT_PROPERTIES_INDEX_NAME))
            }
            DOCUMENT_DB -> {
                snapshots.createIndex(BasicDBObject("$COMMIT_PROPERTIES.key", ASC))
                snapshots.createIndex(BasicDBObject("$COMMIT_PROPERTIES.value", ASC))
            }
        }

        headCollection()
    }

    internal fun snapshotsCollection(): MongoCollection<Document> =
        mongo.getCollection(SNAPSHOTS)

    internal fun headCollection(): MongoCollection<Document> =
        mongo.getCollection(MongoDBHeadId.COLLECTION_NAME)
}