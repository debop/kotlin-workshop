package org.javers

import org.javers.core.commit.CommitId
import org.javers.core.metamodel.`object`.CdoSnapshot
import org.javers.core.metamodel.`object`.SnapshotType
import org.javers.repository.api.QueryParams


fun List<CdoSnapshot>.filterByToCommitId(commitId: CommitId): List<CdoSnapshot> =
    filter { it.commitMetadata.id.isBeforeOrEqual(commitId) }

fun List<CdoSnapshot>.filterByCommitIds(commitIds: Set<CommitId>): List<CdoSnapshot> =
    filter { commitIds.contains(it.commitId) }

fun List<CdoSnapshot>.filterByVersion(version: Long): List<CdoSnapshot> =
    filter { it.version == version }

fun List<CdoSnapshot>.filterByAuthor(author: String): List<CdoSnapshot> =
    filter { it.commitMetadata.author == author }

fun List<CdoSnapshot>.filterByCommitDate(queryParams: QueryParams): List<CdoSnapshot> =
    filter { queryParams.isDateInRange(it.commitMetadata.commitDate) }

fun List<CdoSnapshot>.filterByPropertyName(propertyName: String): List<CdoSnapshot> =
    filter { it.hasChangeAt(propertyName) }

fun List<CdoSnapshot>.filterByType(snapshotType: SnapshotType): List<CdoSnapshot> =
    filter { it.type == snapshotType }

fun List<CdoSnapshot>.filterByCommitProperties(commitProperties: Map<String, String>): List<CdoSnapshot> =
    filter { snapshot ->
        val actualCommitProperties = snapshot.commitMetadata.properties
        commitProperties.all { (key, value) ->
            actualCommitProperties.containsKey(key) && actualCommitProperties[key] == value
        }
    }

fun List<CdoSnapshot>.trimToRequestedSlice(from: Int, size: Int): List<CdoSnapshot> {
    val fromIndex = minOf(from, this.size)
    val toIndex = minOf(from + size, this.size)
    return this.subList(fromIndex, toIndex)
}
