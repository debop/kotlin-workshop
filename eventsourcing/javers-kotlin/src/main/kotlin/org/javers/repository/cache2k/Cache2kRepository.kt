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

/**
 * [JaversRepository] using cache2k to save snapshot
 *
 * @author debop
 * @since 19. 7. 16
 */
class Cache2kRepository : JaversRepository {

    companion object : KLogging()

    private val snapshots: Cache<String, LinkedList<String>> by lazy {
        @Suppress("UNCHECKED_CAST")
        Cache2kBuilder.of(String::class.java, LinkedList::class.java).build() as Cache<String, LinkedList<String>>
    }

    private val commits: Cache<CommitId, Int> by lazy {
        Cache2kBuilder.of(CommitId::class.java, Int::class.java).build()
    }

    private val counter = AtomicInteger()

    private var head: CommitId? = null
    private var jsonConverter: JsonConverter? = null

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

        //        val parent = parentCandidate as EntityType
        //        val child = childCandidate as ValueObjectId
        //
        //        return child.ownerId.typeName == parent.name
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

        snapshots.keys().forEach { all.addAll(readSnapshots(it)) }
        return all.sortedByDescending { getSeq(it.commitMetadata.id) }
    }

    private fun getSeq(commitId: CommitId): Int = commits[commitId]


    override fun getLatest(globalId: GlobalId): Optional<CdoSnapshot> {
        if (contains(globalId)) {
            return Optional.of(readSnapshots(globalId).first())
        }
        return Optional.empty()
    }

    override fun getSnapshots(queryParams: QueryParams): MutableList<CdoSnapshot> {
        return applyQueryParams(getAll(), queryParams)
    }

    override fun getSnapshots(snapshotIdentifiers: MutableCollection<SnapshotIdentifier>): MutableList<CdoSnapshot> {
        return getPersistedIdentifiers(snapshotIdentifiers)
            .map {
                val objectSnapshots = readSnapshots(it.globalId)
                objectSnapshots[objectSnapshots.size - it.version.toInt()]
            }
            .toMutableList()
    }

    private fun getPersistedIdentifiers(snapshotIdentifiers: MutableCollection<SnapshotIdentifier>): List<SnapshotIdentifier> {
        return snapshotIdentifiers
            .filter {
                this.contains(it.globalId) && it.version <= readSnapshots(it.globalId).size
            }
    }

    override fun persist(commit: Commit) {
        logger.debug { "Persist ... commit=$commit" }

        commit.snapshots.forEach {
            persist(it)
        }

        logger.debug { "${commit.snapshots.size} snapshot(s) persisted" }
        head = commit.id
        commits.put(headId, counter.incrementAndGet())
    }

    private fun persist(snapshot: CdoSnapshot) {
        check(jsonConverter != null) { "jsonConverter is null" }

        logger.debug { "Persist snapshot. $snapshot" }
        val snapshotJson = jsonConverter!!.toJson(snapshot)

        synchronized(this) {
            val globalIdValue = snapshot.globalId.value()
            val snapshotsList = snapshots.computeIfAbsent(globalIdValue) { LinkedList<String>() }
            snapshotsList.push(snapshotJson)
        }

        logger.debug { "Persist snapshot as Json:\n$snapshotJson" }
    }

    override fun getHeadId(): CommitId? = head

    override fun setJsonConverter(jsonConverter: JsonConverter?) {
        this.jsonConverter = jsonConverter
    }

    override fun ensureSchema() {
        // Nothing to do
    }

    private fun contains(globalId: GlobalId): Boolean = contains(globalId.value())

    private fun contains(globalIdValue: String): Boolean = snapshots.containsKey(globalIdValue)

    private fun readSnapshots(globalIdValue: String): MutableList<CdoSnapshot> {
        logger.trace { "Read from repository. globalIdValue=$globalIdValue" }
        return snapshots[globalIdValue]
                   ?.map { jsonConverter!!.fromJson(it, CdoSnapshot::class.java) }
                   ?.toMutableList()
               ?: mutableListOf<CdoSnapshot>()
    }

    private fun readSnapshots(globalId: GlobalId): MutableList<CdoSnapshot> {
        return readSnapshots(globalId.value())
    }
}