package io.github.debop.javers.examples

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import org.junit.jupiter.api.Test

/**
 * CommitAndQueryExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 10
 */
class CommitAndQueryExample {

    companion object: KLogging()

    val javers = JaversBuilder.javers().build()

    @Test
    fun `should commit and query from JaversRepository`() {

        val robert = Person("bob", "Robert Martin")
        javers.commit("user", robert)

        robert.name = "Robert C."
        robert.position = Position.Developer
        javers.commit("user", robert)

        val query = QueryBuilder.byInstanceId("bob", Person::class.java).build()

        println("Shadows query:")

        val shadows = javers.findShadows<Person>(query)
        shadows.forEach { println(it.get()) }
        shadows.size shouldBeEqualTo 2

        println("Snapshots query:")
        val snapshots = javers.findSnapshots(query)
        snapshots.forEach { println(it) }
        snapshots.size shouldBeEqualTo 2

        val changes = javers.findChanges(query)
        println(changes.prettyPrint())
        changes.size shouldBeEqualTo 2
    }
}