package org.javers.core

import org.javers.core.commit.CommitId
import java.util.UUID
import java.util.function.Supplier
import kotlin.math.abs

/**
 * RandomCommitGenerator
 *
 * @author debop
 * @since 19. 7. 15
 */
class RandomCommitGenerator: Supplier<CommitId> {

    private val commits = hashMapOf<CommitId, Int>()
    private var counter: Int = 0

    fun getSeq(commitId: CommitId): Int =
        commits[commitId] ?: throw NoSuchElementException("Not found commitId. $commitId")

    override fun get(): CommitId {
        synchronized(this) {
            counter++
            val next = CommitId(abs(UUID.randomUUID().leastSignificantBits), 0)
            commits[next] = counter

            return next
        }
    }
}