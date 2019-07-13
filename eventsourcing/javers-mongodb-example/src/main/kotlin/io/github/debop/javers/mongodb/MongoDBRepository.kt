package io.github.debop.javers.mongodb

import com.google.gson.JsonObject
import com.mongodb.BasicDBObject
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import io.github.debop.javers.mongodb.MongoDBSchemaManager.Companion.GLOBAL_ID_ENTITY
import io.github.debop.javers.mongodb.MongoDBSchemaManager.Companion.GLOBAL_ID_FRAGMENT
import io.github.debop.javers.mongodb.MongoDBSchemaManager.Companion.GLOBAL_ID_KEY
import io.github.debop.javers.mongodb.MongoDBSchemaManager.Companion.GLOBAL_ID_OWNER_ID_ENTITY
import io.github.debop.javers.mongodb.MongoDBSchemaManager.Companion.GLOBAL_ID_VALUE_OBJECT
import io.github.debop.javers.mongodb.MongoDBSchemaManager.Companion.OBJECT_ID
import io.github.debop.javers.mongodb.MongoDBSchemaManager.Companion.SNAPSHOT_VERSION
import io.github.debop.javers.mongodb.model.MongoDBHeadId
import mu.KLogging
import org.bson.Document
import org.bson.conversions.Bson
import org.javers.common.string.RegexEscape
import org.javers.core.JaversCoreConfiguration
import org.javers.core.commit.Commit
import org.javers.core.commit.CommitId
import org.javers.core.json.JsonConverter
import org.javers.core.metamodel.`object`.CdoSnapshot
import org.javers.core.metamodel.`object`.GlobalId
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.ManagedType
import org.javers.core.metamodel.type.ValueObjectType
import org.javers.repository.api.ConfigurationAware
import org.javers.repository.api.JaversRepository
import org.javers.repository.api.QueryParams
import org.javers.repository.api.QueryParamsBuilder
import org.javers.repository.api.SnapshotIdentifier
import java.util.Optional

/**
 * MongoDBRepository
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 12
 */
class MongoDBRepository(mongo: MongoDatabase,
                        cacheSize: Int = DEFAULT_CACHE_SIZE,
                        val dialect: MongoDBDialect = MongoDBDialect.MONGO_DB): JaversRepository, ConfigurationAware {

    companion object: KLogging() {
        private const val DEFAULT_CACHE_SIZE = 5000
        private const val DESC = -1

        fun mongoRepositoryWithDocumentDBCompatibility(mongo: MongoDatabase): MongoDBRepository =
            MongoDBRepository(mongo, DEFAULT_CACHE_SIZE, MongoDBDialect.DOCUMENT_DB)

        private fun <T> getOne(cursor: MongoCursor<T>): Optional<T> = cursor.use {
            if (cursor.hasNext()) Optional.of(cursor.next())
            else Optional.empty()
        }

        private fun prefixQuery(fieldName: String, prefix: String): Bson =
            Filters.regex(fieldName, "^" + RegexEscape.escape(prefix) + ".*")
    }

    private val schemaManager: MongoDBSchemaManager = MongoDBSchemaManager(mongo)
    private var jsonConverter: JsonConverter? = null
    private var coreConfiguration: JaversCoreConfiguration? = null
    private val mapKeyDotReplacer = MapKeyDotReplacer()
    private val cache: LatestSnapshotCache = LatestSnapshotCache(cacheSize) { input -> getLatest(createIdQuery(input)).orElse(null) }

    override fun persist(commit: Commit) {
        logger.debug { "Persist commit. commit=$commit" }
        persistSnapshots(commit)
        persistHeadId(commit)
    }

    internal fun clean() {
        snapshotsCollection().deleteMany(Document())
        headCollection().deleteMany(Document())
    }

    override fun getStateHistory(globalId: GlobalId, queryParams: QueryParams): MutableList<CdoSnapshot> {
        val query = when {
            queryParams.isAggregate -> createIdQueryWithAggregate(globalId)
            else -> createIdQuery(globalId)
        }
        return queryForSnapshots(query, Optional.of(queryParams))
    }

    override fun getLatest(globalId: GlobalId): Optional<CdoSnapshot> {
        return cache.getLatest(globalId)
    }

    override fun getSnapshots(queryParams: QueryParams): MutableList<CdoSnapshot> {
        return queryForSnapshots(BasicDBObject(), Optional.of(queryParams))
    }

    override fun getSnapshots(snapshotIdentifiers: MutableCollection<SnapshotIdentifier>): MutableList<CdoSnapshot> {
        return if (snapshotIdentifiers.isEmpty()) mutableListOf()
        else queryForSnapshots(createSnapshotIdentifiersQuery(snapshotIdentifiers), Optional.empty())
    }

    override fun getValueObjectStateHistory(ownerEntity: EntityType,
                                            path: String,
                                            queryParams: QueryParams): MutableList<CdoSnapshot> {
        val query = BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ownerEntity.name)
        query.append(GLOBAL_ID_FRAGMENT, path)

        return queryForSnapshots(query, Optional.of(queryParams))
    }

    override fun getStateHistory(givenClasses: MutableSet<ManagedType>,
                                 queryParams: QueryParams): MutableList<CdoSnapshot> {
        val query = createManagedTypeQuery(givenClasses, queryParams.isAggregate)
        return queryForSnapshots(query, Optional.of(queryParams))
    }

    override fun getHeadId(): CommitId? {
        val headId = headCollection().find().first()

        return headId?.let { MongoDBHeadId(it).toCommitId() }
    }

    override fun setJsonConverter(jsonConverter: JsonConverter?) {
        this.jsonConverter = jsonConverter
    }

    override fun setConfiguration(coreConfiguration: JaversCoreConfiguration?) {
        this.coreConfiguration = coreConfiguration
    }

    override fun ensureSchema() {
        schemaManager.ensureSchema(dialect)
    }

    private fun createIdQuery(id: GlobalId): Bson {
        return BasicDBObject(GLOBAL_ID_KEY, id.value())
    }

    private fun createIdQueryWithAggregate(id: GlobalId): Bson {
        return Filters.or(createIdQuery(id), prefixQuery(GLOBAL_ID_KEY, id.value() + "#"))
    }

    private fun createVersionQuery(version: Long): Bson {
        return BasicDBObject(SNAPSHOT_VERSION, version)
    }

    private fun createSnapshotIdentifiersQuery(snapshotIdentifiers: MutableCollection<SnapshotIdentifier>): Bson {
        val descFilters = snapshotIdentifiers.map {
            Filters.and(createIdQuery(it.globalId), createVersionQuery(it.version))
        }
        return Filters.or(descFilters)
    }

    private fun createManagedTypeQuery(managedTypes: Set<ManagedType>, aggregate: Boolean): Bson {
        val classFilters = managedTypes.map {
            when (it) {
                is ValueObjectType -> createValueObjectTypeQuery(it)
                else -> createEntityTypeQuery(aggregate, it)
            }
        }

        return Filters.or(classFilters)
    }

    private fun createValueObjectTypeQuery(managedType: ManagedType): Bson {
        return BasicDBObject(GLOBAL_ID_VALUE_OBJECT, managedType.name)
    }

    private fun createEntityTypeQuery(aggregate: Boolean, managedType: ManagedType): Bson {
        var entityTypeQuery = prefixQuery(GLOBAL_ID_KEY, managedType.name + "/")
        if (!aggregate) {
            entityTypeQuery = Filters.and(entityTypeQuery, Filters.exists(GLOBAL_ID_ENTITY))
        }
        return entityTypeQuery
    }

    private fun readFromDBObject(dbObject: Document): CdoSnapshot {
        check(jsonConverter != null) { "MongoDBRepository: jsonConverter is null." }

        val jsonElement = DocumentConverter.fromDocument(mapKeyDotReplacer.back(dbObject))
        return jsonConverter!!.fromJson(jsonElement, CdoSnapshot::class.java)
    }

    private fun writeToDBObject(snapshot: CdoSnapshot): Document {
        logger.debug { "Write to document. snapshot=$snapshot" }
        check(jsonConverter != null) { "MongoDBRepository: jsonConverter is null" }
        var dbObject = DocumentConverter.toDocument(jsonConverter!!.toJsonElement(snapshot) as JsonObject)
        dbObject = mapKeyDotReplacer.replaceInSnapshotState(dbObject)
        dbObject.append(GLOBAL_ID_KEY, snapshot.globalId.value())
        return dbObject
    }

    private fun snapshotsCollection(): MongoCollection<Document> {
        return schemaManager.snapshotsCollection()
    }

    private fun headCollection(): MongoCollection<Document> {
        return schemaManager.headCollection()
    }

    private fun persistSnapshots(commit: Commit) {
        TODO("구현 중")
    }

    private fun persistHeadId(commit: Commit) {
        TODO("구현 중")
    }

    private fun objectIdFilter(document: Document): Bson {
        return Filters.eq(OBJECT_ID, document.getObjectId(OBJECT_ID))
    }

    private fun getMongoSnapshotsCursor(query: Bson, queryParams: Optional<QueryParams>): MongoCursor<Document> {
        TODO("구현 중")
    }

    private fun applyQueryParams(query: Bson, queryParams: Optional<QueryParams>): Bson {
        TODO("구현 중")
    }

    private fun applyQueryParams(findIterable: FindIterable<Document>,
                                 queryParams: Optional<QueryParams>): FindIterable<Document> {
        TODO("구현 중")
    }

    private fun addCommitPropertiesFilter(query: Bson, commitProperties: Map<String, String>): Bson {
        TODO("구현 중")
    }

    private fun getLatest(idQuery: Bson): Optional<CdoSnapshot> {
        val queryParams = QueryParamsBuilder.withLimit(1).build()
        val mongoLatest = getMongoSnapshotsCursor(idQuery, Optional.of(queryParams))

        return getOne(mongoLatest).map { readFromDBObject(it) }
    }

    private fun queryForSnapshots(query: Bson, queryParams: Optional<QueryParams>): MutableList<CdoSnapshot> {
        TODO("구현 중")
    }
}