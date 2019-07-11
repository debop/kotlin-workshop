package io.github.debop.javers.examples

import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.`object`.InstanceId
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.metamodel.type.EntityType
import org.junit.jupiter.api.Test

/**
 * MappingExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 11
 */
class MappingExample {

    @TypeName("Person")
    class Person(
        @Id
        var name: String? = null,
        var position: String? = null
    )

    @Test
    fun `should map Person as EntityType`() {
        val bob = Person(name = "Bob", position = "dev")
        val javers = JaversBuilder.javers().build()

        val personType = javers.getTypeMapping<EntityType>(Person::class.java)
        val bobId = personType.createIdFromInstance(bob)

        println("JaversType of Person: ${personType.prettyPrint()}")
        println("Id of bob: `${bobId.value()}`")

        bobId.value() shouldEqual "Person/Bob"
        bobId shouldBeInstanceOf InstanceId::class
    }

    @TypeName("Entity")
    class Entity(@Id var id: Point, var data: String? = null)

    class Point(val x: Double, val y: Double) {
        fun myToString() = "(${x.toInt()},${y.toInt()})"
    }

    @Test
    fun `should use String representation of complex Id instead of its equals()`() {
        // GIVEN
        val p1 = Point(1.0, 3.0)
        val p2 = Point(1.0, 3.0)

        val entity1 = Entity(p1)
        val entity2 = Entity(p2)

        val javers = JaversBuilder.javers().build()

        println("p1.equals(p2): ${p1.equals(p2)}")
        println("GlobalId of entity1: ${javers.getTypeMapping<EntityType>(Entity::class.java).createIdFromInstance(entity1).value()}")
        println("GlobalId of entity2: ${javers.getTypeMapping<EntityType>(Entity::class.java).createIdFromInstance(entity2).value()}")

        p1 shouldNotEqual p2
        javers.compare(entity1, entity2).changes.shouldBeEmpty()
    }

    @Test
    fun `should use custom toString function for complex Id`() {
        val entity = Entity(Point(1.0 / 3, 4.0 / 3))

        // default reflectiveToString function
        var javers = JaversBuilder.javers().build()
        var id = javers.getTypeMapping<EntityType>(Entity::class.java).createIdFromInstance(entity)

        id.value() shouldEqual "Entity/0.3333333333333333,1.3333333333333333"

        // when custom toString function
        javers = JaversBuilder.javers()
            .registerValueWithCustomToString(Point::class.java, { it.myToString() })
            .build()

        id = javers.getTypeMapping<EntityType>(Entity::class.java).createIdFromInstance(entity)

        id.value() shouldEqual "Entity/(0,1)"
    }
}