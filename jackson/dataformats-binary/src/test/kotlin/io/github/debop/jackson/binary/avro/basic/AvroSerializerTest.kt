package io.github.debop.jackson.binary.avro.basic

import io.github.debop.avro.examples.Employee
import io.github.debop.avro.examples.ProductProperty
import io.github.debop.avro.examples.ProductRoot
import io.github.debop.jackson.binary.avro.DefaultAvroSerializer
import io.github.debop.jackson.binary.avro.deserialize
import io.github.debop.jackson.binary.avro.deserializeList
import io.github.debop.kotlin.tests.extensions.Random
import io.github.debop.kotlin.tests.extensions.Randomized
import mu.KLogging
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.apache.avro.specific.SpecificRecord
import org.junit.jupiter.api.RepeatedTest

import io.github.debop.avro.examples.v1.VersionedItem as ItemV1
import io.github.debop.avro.examples.v2.VersionedItem as ItemV2

/**
 * AvroSerializerTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 29
 */
@Randomized
class AvroSerializerTest: AbstractAvroTest() {

    companion object: KLogging() {
        private const val TEST_COUNT = 10
    }

    val serializer = DefaultAvroSerializer()

    @RepeatedTest(TEST_COUNT)
    fun `serialize single object`(@Random employee: Employee) {
        verifySerialization(employee)
    }

    @RepeatedTest(TEST_COUNT)
    fun `serialize collection of avro object`(@Random(type = Employee::class) emps: List<Employee>) {
        val bytes = serializer.serializeList(emps)
        bytes.shouldNotBeEmpty()

        val converted = serializer.deserializeList<Employee>(bytes)
        converted shouldContainAll emps
    }

    @RepeatedTest(TEST_COUNT)
    fun `serialize nested object`(@Random productRoot: ProductRoot,
                                  @Random(type = ProductProperty::class) productProperties: MutableList<ProductProperty>) {
        productRoot.productProperties = productProperties
        verifySerialization(productRoot)
    }

    @RepeatedTest(TEST_COUNT)
    fun `serialize v1 object`(@Random v1: ItemV1) {
        verifySerialization(v1)
    }

    @RepeatedTest(TEST_COUNT)
    fun `serialize v2 object`(@Random v2: ItemV2) {
        verifySerialization(v2)
    }

    private inline fun <reified T: SpecificRecord> verifySerialization(avroObject: T) {
        val bytes = serializer.serialize(avroObject)
        bytes.shouldNotBeNull()

        val converted = serializer.deserialize<T>(bytes)
        converted shouldEqual avroObject
    }

    @RepeatedTest(TEST_COUNT)
    fun `serialize v1 and deserialize as v2`(@Random v1: ItemV1) {
        val bytes = serializer.serialize(v1)

        val convertedV2 = serializer.deserialize<ItemV2>(bytes)
        convertedV2.shouldNotBeNull()
        convertedV2.id shouldEqual v1.id
        convertedV2.key shouldEqual v1.key
    }

    @RepeatedTest(TEST_COUNT)
    fun `serialize v2 and deserialize as v1`(@Random v2: ItemV2) {
        val bytes = serializer.serialize(v2)

        val convertedV1 = serializer.deserialize<ItemV1>(bytes)
        convertedV1.shouldNotBeNull()
        convertedV1.id shouldEqual v2.id
        convertedV1.key shouldEqual v2.key
    }
}