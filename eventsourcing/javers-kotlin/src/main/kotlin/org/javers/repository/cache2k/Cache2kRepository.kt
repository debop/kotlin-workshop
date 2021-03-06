package org.javers.repository.cache2k

import mu.KLogging
import org.cache2k.Cache
import org.cache2k.Cache2kBuilder
import org.javers.core.commit.Commit
import org.javers.core.commit.CommitId
import org.javers.core.json.JsonConverter
import org.javers.core.metamodel.`object`.CdoSnapshot
import org.javers.core.metamodel.`object`.GlobalId
import org.javers.core.metamodel.`object`.InstanceId
import org.javers.core.metamodel.`object`.ValueObjectId
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.ManagedType
import org.javers.filterByAuthor
import org.javers.filterByCommitDate
import org.javers.filterByCommitIds
import org.javers.filterByCommitProperties
import org.javers.filterByPropertyName
import org.javers.filterByToCommitId
import org.javers.filterByType
import org.javers.filterByVersion
import org.javers.repository.api.JaversRepository
import org.javers.repository.api.QueryParams
import org.javers.repository.api.SnapshotIdentifier
import org.javers.trimToRequestedSlice
import java.util.LinkedList
import java.util.Optional
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/**
 * [JaversRepository] using cache2k to save snapshot
 *
 * @author debop
 * @since 19. 7. 16
 */
class Cache2kRepository : JaversRepository {

    companion object : KLogging()

    /**
     * Snapshot을 저장하는 Cache (key=globalId, value=snapshot list)
     */
    private val snapshotCache: Cache<String, LinkedList<String>> by lazy {
        @Suppress("UNCHECKED_CAST")
        Cache2kBuilder.of(String::class.java, LinkedList::class.java).build() as Cache<String, LinkedList<String>>
    }
    /**
     * [CommitId] - Sequence Number 를 캐시합니다.
     */
    private val commitCache: Cache<CommitId, Int> by lazy {
        Cache2kBuilder.of(CommitId::class.java, Int::class.java).build()
    }

    private val counter = AtomicInteger()

    private var head: CommitId? = null
    private var jsonConverter: JsonConverter? = null
    private val syncObj: ReadWriteLock = ReentrantReadWriteLock()

    override fun getValueObjectStateHistory(ownerEntity: EntityType, path: String, queryParams: QueryParams): MutableList<CdoSnapshot> {

        val result = getAll().filter {
            val id = it.globalId
            if (id is ValueObjectId) {
                id.hasOwnerOfType(ownerEntity) && id.fragment.equals(path)
            } else {
                false
            }
        }

        return applyQueryParams(result, queryParams)
    }

    override fun getStateHistory(globalId: GlobalId, queryParams: QueryParams): MutableList<CdoSnapshot> {
        val filtered = mutableListOf<CdoSnapshot>()

        getAll().forEach {
            if (it.globalId == globalId) {
                filtered.add(it)
            }
            if (queryParams.isAggregate && isParent(globalId, it.globalId)) {
                filtered.add(it)
            }
        }

        return applyQueryParams(filtered, queryParams)
    }

    override fun getStateHistory(givenClasses: MutableSet<ManagedType>, queryParams: QueryParams): MutableList<CdoSnapshot> {
        val filtered = mutableListOf<CdoSnapshot>()

        getAll().forEach { snapshot ->
            givenClasses.forEach { givenClass ->
                if (snapshot.globalId.isTypeOf(givenClass)) {
                    filtered.add(snapshot)
                }
                if (queryParams.isAggregate && isParent(givenClass, snapshot.globalId)) {
                    filtered.add(snapshot)
                }
            }
        }

        return applyQueryParams(filtered, queryParams)
    }

    private fun isParent(parentCandidate: GlobalId, childCandidate: GlobalId): Boolean {
        if (!(parentCandidate is InstanceId && childCandidate is ValueObjectId)) {
            return false
        }

        return childCandidate.ownerId == parentCandidate
    }

    private fun isParent(parentCandidate: ManagedType, childCandidate: GlobalId): Boolean {
        if (!(parentCandidate is EntityType && childCandidate is ValueObjectId)) {
            return false
        }

        return childCandidate.ownerId == parentCandidate
    }

    private fun applyQueryParams(snapshots: List<CdoSnapshot>, queryParams: QueryParams): MutableList<CdoSnapshot> {
        var result = snapshots
        if (queryParams.commitIds().isNotEmpty()) {
            result = result.filterByCommitIds(queryParams.commitIds())
        }
        if (queryParams.toCommitId().isPresent) {
            result = result.filterByToCommitId(queryParams.toCommitId().get())
        }
        if (queryParams.version().isPresent) {
            result = result.filterByVersion(queryParams.version().get())
        }
        if (queryParams.author().isPresent) {
            result = result.filterByAuthor(queryParams.author().get())
        }
        if (queryParams.hasDates()) {
            result = result.filterByCommitDate(queryParams)
        }
        if (queryParams.changedProperty().isPresent) {
            result = result.filterByPropertyName(queryParams.changedProperty().get())
        }
        if (queryParams.snapshotType().isPresent) {
            result = result.filterByType(queryParams.snapshotType().get())
        }
        result = result.filterByCommitProperties(queryParams.commitProperties())

        return result.trimToRequestedSlice(queryParams.skip(), queryParams.limit()).toMutableList()
    }

    private fun getAll(): List<CdoSnapshot> {
        val all = mutableListOf<CdoSnapshot>()

        snapshotCache.keys().forEach { all.addAll(readSnapshots(it)) }
        return all.sortedByDescending { getSeq(it.commitMetadata.id) }
    }

    private fun getSeq(commitId: CommitId): Int = commitCache[commitId]


    override fun getLatest(globalId: GlobalId): Optional<CdoSnapshot> {
        if (contains(globalId)) {
            return Optional.of(readSnapshots(globalId).first())
        }
        return Optional.empty()
    }

    override fun getSnapshots(queryParams: QueryParams): MutableList<CdoSnapshot> {
        return applyQueryParams(getAll(), queryParams)
    }

    override fun getSnapshots(snapshotIdentifiers: Collection<SnapshotIdentifier>): List<CdoSnapshot> {
        return getPersistedIdentifiers(snapshotIdentifiers)
            .map {
                val objectSnapshots = readSnapshots(it.globalId)
                objectSnapshots[objectSnapshots.size - it.version.toInt()]
            }
    }

    private fun getPersistedIdentifiers(snapshotIdentifiers: Collection<SnapshotIdentifier>): List<SnapshotIdentifier> {
        return snapshotIdentifiers
            .filter {
                this.contains(it.globalId) && it.version <= snapshotCache[it.globalId.value()].size
            }
    }

    override fun persist(commit: Commit) {
        logger.trace { "Persist ... commit=$commit" }

        syncObj.writeLock().withLock {
            commit.snapshots.forEach {
                persist(it)
            }
            head = commit.id
            commitCache.put(headId, counter.incrementAndGet())
        }

        logger.trace { "${commit.snapshots.size} snapshot(s) persisted" }
    }

    private fun persist(snapshot: CdoSnapshot) {
        check(jsonConverter != null) { "jsonConverter is null" }

        logger.trace { "Persist snapshot. $snapshot" }

        // NOTE: snapshot 저장은 Json Format으로 하는 것이 기본이다.
        // TODO: Local 에서는 Binary Serialization과 압축을 이용하면 좋을 듯
        // TODO: Kafka 전송 시에는 그냥 Json Format을 보내던가 Avro 포맷으로 변경하던가 해야겠다 (Kafka Key에 CommitId 를 넣도록 하자))
        val snapshotJson = jsonConverter!!.toJson(snapshot)

        val globalIdValue = snapshot.globalId.value()
        val snapshotsList = snapshotCache.computeIfAbsent(globalIdValue) { LinkedList<String>() }
        snapshotsList.push(snapshotJson)

        logger.trace { "Persist snapshot as Json:\n$snapshotJson" }
    }

    override fun getHeadId(): CommitId? = head

    override fun setJsonConverter(jsonConverter: JsonConverter?) {
        this.jsonConverter = jsonConverter
    }

    override fun ensureSchema() {
        // Nothing to do
    }

    private fun contains(globalId: GlobalId): Boolean = contains(globalId.value())

    private fun contains(globalIdValue: String): Boolean = snapshotCache.containsKey(globalIdValue)

    private fun readSnapshots(globalIdValue: String): List<CdoSnapshot> {
        // logger.trace { "Read from repository. globalIdValue=$globalIdValue" }
        return snapshotCache[globalIdValue]?.map { jsonConverter!!.fromJson(it, CdoSnapshot::class.java) }
               ?: emptyList()
    }

    private fun readSnapshots(globalId: GlobalId): List<CdoSnapshot> {
        return readSnapshots(globalId.value())
    }
}
