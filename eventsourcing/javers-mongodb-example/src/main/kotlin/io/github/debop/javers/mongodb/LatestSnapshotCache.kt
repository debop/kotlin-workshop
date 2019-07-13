package io.github.debop.javers.mongodb

import org.cache2k.Cache2kBuilder
import org.javers.core.metamodel.`object`.CdoSnapshot
import org.javers.core.metamodel.`object`.GlobalId
import java.util.Optional

/**
 * LatestSnapshotCache
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 12
 */
class LatestSnapshotCache(private val size: Int,
                          private val source: (GlobalId) -> CdoSnapshot?) {
    private val cache = Cache2kBuilder
        .of(GlobalId::class.java, CdoSnapshot::class.java)
        .entryCapacity(size.toLong())
        .loader(source)
        .build()

    private val disabled = size <= 0

    internal fun getLatest(globalId: GlobalId): Optional<CdoSnapshot> {
        if (disabled) {
            return Optional.ofNullable(source.invoke(globalId))
        }

        return Optional.ofNullable(cache.get(globalId))
    }

    internal fun put(cdoSnapshot: CdoSnapshot) {
        if (!disabled) {
            cache.put(cdoSnapshot.globalId, cdoSnapshot)
        }
    }
}