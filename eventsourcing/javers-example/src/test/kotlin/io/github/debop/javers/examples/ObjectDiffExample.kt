package io.github.debop.javers.examples

import mu.KLogging
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEndWith
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.junit.jupiter.api.Test

/**
 * ObjectDiffExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 10
 */
class ObjectDiffExample {

    companion object: KLogging()

    val javers = JaversBuilder.javers().build()

    @Test
    fun `different two domain objects`() {
        val tommyOld = Person("tommy", "Tommy Smart")
        val tommyNew = Person("tommy", "Tommy C. Smart")

        val diff = javers.compare(tommyOld, tommyNew)

        logger.debug { "diff=$diff" }
        diff.changes.size shouldEqualTo 1

        logger.debug { javers.jsonConverter.toJson(diff) }
    }

    @Test
    fun `detect added of hierarchy`() {
        val oldBoss = Employee("Big Boss").apply {
            addSubordinates(Employee("Great Developer"))
        }
        val newBoss = Employee("Big Boss").apply {
            addSubordinates(Employee("Great Developer"),
                            Employee("Hired One"),
                            Employee("Hired Second"))
        }

        val diff = javers.compare(oldBoss, newBoss)
        logger.debug { "Hierarchy diff=$diff" }

        diff.getObjectsByChangeType(NewObject::class.java) shouldContainSame listOf(Employee("Hired One"), Employee("Hired Second"))

        logger.debug { "Hierarchy diff=\n${javers.jsonConverter.toJson(diff)}" }
    }

    @Test
    fun `detect fired of employee`() {
        val oldBoss = Employee("Big Boss").apply {
            addSubordinates(
                Employee("Great Developer"),
                Employee("Team Leader").apply {
                    addSubordinates(Employee("Another Dev"),
                                    Employee("To Be Fired"))
                })
        }

        val newBoss = Employee("Big Boss").apply {
            addSubordinates(
                Employee("Great Developer"),
                Employee("Team Leader").apply {
                    addSubordinates(Employee("Another Dev"))
                })
        }

        val diff = javers.compare(oldBoss, newBoss)
        logger.debug { "Removed diff=$diff" }
        logger.debug { "Removed diff=\n${javers.jsonConverter.toJson(diff)}" }

        val removed = diff.getChangesByType(ObjectRemoved::class.java)
        removed.size shouldEqualTo 1
        removed.forEach {
            logger.trace { "removed=$it" }
            logger.trace { it.affectedObject }
        }
        removed[0].affectedObject.get() shouldEqual Employee("To Be Fired")
    }

    @Test
    fun `should detect salary changed`() {
        val oldBoss = Employee("Big Boss").apply {
            addSubordinates(Employee("Noisy Manager"),
                            Employee("Great Developer", 10_000))
        }

        val newBoss = Employee("Big Boss").apply {
            addSubordinates(Employee("Noisy Manager"),
                            Employee("Great Developer", 20_000))
        }

        val diff = javers.compare(oldBoss, newBoss)
        val valueChanged = diff.getChangesByType(ValueChange::class.java)[0]

        valueChanged.affectedLocalId shouldEqual "Great Developer"
        valueChanged.propertyName shouldEqual "salary"
        valueChanged.left shouldEqual 10_000
        valueChanged.right shouldEqual 20_000
    }

    @Test
    fun `should detect boss change`() {
        val oldBoss = Employee("Big Boss").apply {
            addSubordinates(
                Employee("Manager One").apply { addSubordinates(Employee("Great Developer")) },
                Employee("Manager Two")
            )
        }

        val newBoss = Employee("Big Boss").apply {
            addSubordinates(
                Employee("Manager One"),
                Employee("Manager Two").apply { addSubordinates(Employee("Great Developer")) }
            )
        }

        val diff = javers.compare(oldBoss, newBoss)
        logger.debug { "diff=$diff" }

        val change = diff.getChangesByType(ReferenceChange::class.java)[0]

        change.affectedLocalId shouldEqual "Great Developer"
        change.left.value() shouldEndWith "Manager One"
        change.right.value() shouldEndWith "Manager Two"
    }

    @Test
    fun `should compare two value objects`() {
        val address1 = Address("New York", "5th Avenue")
        val address2 = Address("New York", "6th Avenue")

        val diff = javers.compare(address1, address2)
        diff.changes.size shouldEqualTo 1

        val change = diff.getChangesByType(ValueChange::class.java)[0]
        change.affectedGlobalId.value() shouldEqual "${Address::class.java.name}/"
        change.propertyName shouldEqual "street"
        change.left shouldEqual "5th Avenue"
        change.right shouldEqual "6th Avenue"
    }

    @Test
    fun `should compare top-level collections`() {

        val oldList = listOf(Person("tommy", "Tommy Smart"))
        val newList = listOf(Person("tommy", "Tommy C. Smart"))

        val diff = javers.compareCollections(oldList, newList, Person::class.java)
        val change = diff.getChangesByType(ValueChange::class.java)[0]

        logger.debug { "diff=$diff" }

        diff.changes.size shouldEqualTo 1
        change.propertyName shouldEqual "name"
        change.left shouldEqual "Tommy Smart"
        change.right shouldEqual "Tommy C. Smart"
    }
}