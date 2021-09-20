package io.github.debop.javers.examples

import mu.KLogging
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeEqualTo
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

    companion object : KLogging()

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
            shadowE1.id shouldBeEqualTo 1
            shadowE1.ref.shouldBeNull()
        }

        @Test
        fun `commit-deep+1 scope query`() {
            val shadows = javers.findShadows<Entity>(byInstanceId(1, Entity::class.java).withScopeDeepPlus(1).build())

            val shadowE1 = shadows[0].get()

            shadowE1 shouldBeInstanceOf Entity::class
            shadowE1.id shouldBeEqualTo 1
            shadowE1.ref!!.id shouldBeEqualTo 2
            shadowE1.ref!!.ref.shouldBeNull()
        }

        @Test
        fun `commit-deep+3 scope query`() {
            val shadows = javers.findShadows<Entity>(byInstanceId(1, Entity::class.java).withScopeDeepPlus(3).build())

            val shadowE1 = shadows[0].get()

            shadowE1 shouldBeInstanceOf Entity::class
            shadowE1.id shouldBeEqualTo 1
            shadowE1.ref!!.id shouldBeEqualTo 2
            shadowE1.ref!!.ref!!.id shouldBeEqualTo 3
            shadowE1.ref!!.ref!!.ref!!.id shouldBeEqualTo 4
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
            shadowE1.id shouldBeEqualTo 1
            shadowE1.ref.shouldBeNull()
        }

        @Test
        fun `commit-deep scope query`() {
            val shadows = javers.findShadows<Entity>(byInstanceId(1, Entity::class.java).withScopeCommitDeep().build())

            val shadowE1 = shadows[0].get()

            shadowE1 shouldBeInstanceOf Entity::class
            shadowE1.id shouldBeEqualTo 1
            shadowE1.ref!!.id shouldBeEqualTo 2
            shadowE1.ref!!.ref!!.id shouldBeEqualTo 3
            shadowE1.ref!!.ref!!.ref!!.id shouldBeEqualTo 4
        }
    }

    @Nested
    inner class EmployeeTest {

        @Test
        fun `query for changes by any domain object`() {
            val javers = JaversBuilder.javers().build()

            val bob = Employee(name = "bob", salary = 1000).apply {
                primaryAddress = Address("London")
            }
            javers.commit("author", bob)

            bob.salary = 2000
            bob.primaryAddress!!.city = "Paris"
            javers.commit("author", bob)

            val changes = javers.findChanges(QueryBuilder.anyDomainObject().build())

            changes.size shouldBeEqualTo 2
            println(changes.prettyPrint())

            val filtered = changes.getChangesByType(ValueChange::class.java)
            val salaryChange = filtered.find { it.propertyName == "salary" }!!
            val cityChange = filtered.find { it.propertyName == "city" }!!

            salaryChange.left shouldBeEqualTo 1000
            salaryChange.right shouldBeEqualTo 2000
            cityChange.left shouldBeEqualTo "London"
            cityChange.right shouldBeEqualTo "Paris"
        }

        @Test
        fun `query for shadows by instance`() {
            val javers = JaversBuilder.javers().build()

            val bob = Employee(name = "bob", salary = 1000).apply {
                primaryAddress = Address("London")
            }
            javers.commit("author", bob)

            bob.salary = 2000
            bob.primaryAddress!!.city = "Paris"
            javers.commit("author", bob)

            val shadows = javers.findShadows<Employee>(QueryBuilder.byInstance(bob).build())

            shadows.size shouldBeEqualTo 2
            val bobNew: Employee = shadows[0].get()
            val bobOld: Employee = shadows[1].get()

            bobNew.salary shouldBeEqualTo 2000
            bobOld.salary shouldBeEqualTo 1000
            bobNew.primaryAddress?.city shouldBeEqualTo "Paris"
            bobOld.primaryAddress?.city shouldBeEqualTo "London"

            shadows[0].commitMetadata.id.majorId shouldBeEqualTo 2
            shadows[1].commitMetadata.id.majorId shouldBeEqualTo 1
        }

        @Test
        fun `query for shadows with different scopes`() {
            val javers = JaversBuilder.javers().build()

            //    /-> John -> Steve
            // Bob
            //    \-> #address
            val steve = Employee(name = "steve")
            val john = Employee(name = "john").apply { boss = steve }
            val bob = Employee(name = "bob").apply {
                boss = john;
                primaryAddress = Address("London")
            }

            javers.commit("author", steve)  // commit 1.0 with snapshot of Steve
            javers.commit("author", bob)    // commit 2.0 with snapshot of Bob, Bob#address and John

            bob.salary = 1200                       // the change
            javers.commit("author", bob)     // commit 3.0 with snapshot of Bob

            // WHEN `Shallow scope query`
            var shadows = javers.findShadows<Employee>(QueryBuilder.byInstance(bob).build())
            println(shadows.joinToString { it.get().toString() })
            var bobShadow = shadows[0].get()    // Bob의 최신 버전을 가진다

            // THEN
            shadows.size shouldBeEqualTo 2
            bobShadow.name shouldBeEqualTo "bob"
            // 참조객체는 쿼리 범위에서 벗어났으므로 null을 가진다
            bobShadow.boss.shouldBeNull()
            // 자식으로 있는 `Value Object`는 항상 scope에 포함된다 
            bobShadow.primaryAddress?.city shouldBeEqualTo "London"

            // WHEN `commit-deep scope query`
            shadows = javers.findShadows<Employee>(QueryBuilder.byInstance(bob).withScopeCommitDeep().build())
            bobShadow = shadows[0].get()

            // THEN
            bobShadow.boss?.name shouldBeEqualTo "john"  // John is inside the query scope, so his shadow is loaded and linked to Bob
            bobShadow.boss?.boss.shouldBeNull() // Steve is still outside the scope
            bobShadow.primaryAddress?.city shouldBeEqualTo "London"

            // WHEN `deep+2 scope query`
            shadows = javers.findShadows<Employee>(QueryBuilder.byInstance(bob).withScopeDeepPlus(2).build())
            bobShadow = shadows[0].get()

            // THEN
            bobShadow.boss?.name shouldBeEqualTo "john"
            bobShadow.boss?.boss?.name shouldBeEqualTo "steve"  // Steve is loaded thanks to deep+2 scope
            bobShadow.primaryAddress?.city shouldBeEqualTo "London"
        }
    }
}