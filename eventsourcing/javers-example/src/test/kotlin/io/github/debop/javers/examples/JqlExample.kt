package io.github.debop.javers.examples

import mu.KLogging
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.Id
import org.javers.repository.jql.QueryBuilder
import org.javers.repository.jql.QueryBuilder.byInstanceId
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * JqlExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 10
 */
class JqlExample {

    companion object: KLogging()

    class Entity(@Id val id: Int, var ref: Entity? = null)

    val e4 = Entity(4)
    val e3 = Entity(3, e4)
    val e2 = Entity(2, e3)
    val e1 = Entity(1, e2)

    @Nested
    inner class MultipleCommit {

        val javers = JaversBuilder.javers().build()

        @BeforeAll
        fun setup() {
            // E1 -> E2 -> E3 -> E4
            javers.commit("author", e4) // commit 1.0 with e4 snapshot
            javers.commit("author", e3) // commit 2.0 with e3 snapshot
            javers.commit("author", e1) // commit 3.0 with snapshots of e1 and e2
        }

        @Test
        fun `shallow scope query`() {
            val shadows = javers.findShadows<Entity>(byInstanceId(1, Entity::class.java).build())
            var shadowE1 = shadows[0].get()

            shadowE1 shouldBeInstanceOf Entity::class
            shadowE1.id shouldEqualTo 1
            shadowE1.ref.shouldBeNull()
        }

        @Test
        fun `commit-deep+1 scope query`() {
            val shadows = javers.findShadows<Entity>(byInstanceId(1, Entity::class.java).withScopeDeepPlus(1).build())

            val shadowE1 = shadows[0].get()

            shadowE1 shouldBeInstanceOf Entity::class
            shadowE1.id shouldEqualTo 1
            shadowE1.ref!!.id shouldEqualTo 2
            shadowE1.ref!!.ref.shouldBeNull()
        }

        @Test
        fun `commit-deep+3 scope query`() {
            val shadows = javers.findShadows<Entity>(byInstanceId(1, Entity::class.java).withScopeDeepPlus(3).build())

            val shadowE1 = shadows[0].get()

            shadowE1 shouldBeInstanceOf Entity::class
            shadowE1.id shouldEqualTo 1
            shadowE1.ref!!.id shouldEqualTo 2
            shadowE1.ref!!.ref!!.id shouldEqualTo 3
            shadowE1.ref!!.ref!!.ref!!.id shouldEqualTo 4
        }
    }

    @Nested
    inner class SingleCommit {

        val javers = JaversBuilder.javers().build()

        @BeforeAll
        fun setup() {
            // E1 -> E2 -> E3 -> E4
            javers.commit("author", e1)  //
        }

        @Test
        fun `shallow scope query`() {
            val shadows = javers.findShadows<Entity>(byInstanceId(1, Entity::class.java).build())
            val shadowE1 = shadows[0].get()
            shadowE1 shouldBeInstanceOf Entity::class
            shadowE1.id shouldEqualTo 1
            shadowE1.ref.shouldBeNull()
        }

        @Test
        fun `commit-deep scope query`() {
            val shadows = javers.findShadows<Entity>(byInstanceId(1, Entity::class.java).withScopeCommitDeep().build())

            val shadowE1 = shadows[0].get()

            shadowE1 shouldBeInstanceOf Entity::class
            shadowE1.id shouldEqualTo 1
            shadowE1.ref!!.id shouldEqualTo 2
            shadowE1.ref!!.ref!!.id shouldEqualTo 3
            shadowE1.ref!!.ref!!.ref!!.id shouldEqualTo 4
        }
    }

    @Nested
    inner class EmployeeTest {

        @Test
        fun `should query for Changes made on any object`() {

            val javers = JaversBuilder.javers().build()

            val bob = Employee(name = "bob", salary = 1000).apply {
                primaryAddress = Address("London")
            }
            javers.commit("author", bob)

            bob.salary = 2000
            bob.primaryAddress!!.city = "Paris"
            javers.commit("author", bob)

            val changes = javers.findChanges(QueryBuilder.anyDomainObject().build())

            changes.size shouldEqualTo 2
            println(changes.prettyPrint())

            val filtered = changes.getChangesByType(ValueChange::class.java)
            val salaryChange = filtered.find { it.propertyName == "salary" }!!
            val cityChange = filtered.find { it.propertyName == "city" }!!

            salaryChange.left shouldEqual 1000
            salaryChange.right shouldEqual 2000
            cityChange.left shouldEqual "London"
            cityChange.right shouldEqual "Paris"
        }

        @Test
        fun `query by instance for Shadow`() {

            val javers = JaversBuilder.javers().build()

            val bob = Employee(name = "bob", salary = 1000).apply {
                primaryAddress = Address("London")
            }
            javers.commit("author", bob)

            bob.salary = 2000
            bob.primaryAddress!!.city = "Paris"
            javers.commit("author", bob)

            val shadows = javers.findShadows<Employee>(QueryBuilder.byInstance(bob).build())

            shadows.size shouldEqualTo 2
            val bobNew: Employee = shadows[0].get()
            val bobOld: Employee = shadows[1].get()

            bobNew.salary shouldEqualTo 2000
            bobOld.salary shouldEqualTo 1000
            bobNew.primaryAddress?.city shouldEqual "Paris"
            bobOld.primaryAddress?.city shouldEqual "London"

            shadows[0].commitMetadata.id.majorId shouldEqualTo 2
            shadows[1].commitMetadata.id.majorId shouldEqualTo 1
        }
    }
}