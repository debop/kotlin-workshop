package io.github.debop.jackson.module.kotlin

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

/**
 * KotlinFeatures
 *
 * @author debop
 * @since 19. 6. 28
 */
class KotlinFeatures {

    companion object: KLogging()

    private val mapper = jacksonObjectMapper()
        .configure(SerializationFeature.INDENT_OUTPUT, false)

    private data class BasicPerson(val name: String, val age: Int)

    @Test
    fun `all inference forms`() {
        val json = """{"name":"John Smith", "age":30}"""

        val inferRightSide = mapper.readValue<BasicPerson>(json)
        val inferLeftSide: BasicPerson = mapper.readValue(json)
        val person = mapper.readValue<BasicPerson>(json)

        val expected = BasicPerson("John Smith", 30)

        inferRightSide shouldEqual expected
        inferLeftSide shouldEqual expected
        person shouldEqual expected
    }

    @Test
    fun `read array person`() {
        val json = """[{"name":"John Smith", "age":30}, {"name":"Sunghyouk Bae", "age":51}]"""

        val persons: List<BasicPerson> = mapper.readValue(json)
        persons.size shouldEqualTo 2
        persons shouldContainAll listOf(BasicPerson("John Smith", 30),
                                        BasicPerson("Sunghyouk Bae", 51))
    }

    private data class ClassWithPair(val name: Pair<String, String>, val age: Int)

    @Test
    fun `read Pair`() {

        val expected = """{"name":{"first":"John","second":"Smith"},"age":30}"""
        val input = ClassWithPair(Pair("John", "Smith"), 30)

        val json = mapper.writeValueAsString(input)
        logger.debug { "json=$json" }
        json shouldEqual expected

        val output = mapper.readValue<ClassWithPair>(json)
        output shouldEqual input
    }

    private data class ClassWithPairMixedTypes(val person: Pair<String, Int>)

    @Test
    fun `read pair mixed types`() {
        val json = """{"person":{"first":"John","second":30}}"""
        val expected = ClassWithPairMixedTypes("John" to 30)

        mapper.writeValueAsString(expected) shouldEqual json

        val parsed = mapper.readValue<ClassWithPairMixedTypes>(json)
        parsed shouldEqual expected
    }


    data class ClassWithRanges(val ages: IntRange, val distance: LongRange)

    @Test
    fun `read range`() {
        val expected = ClassWithRanges(18..40, 5L..50L)
        val json = mapper.writeValueAsString(expected)

        logger.trace { "json=$json" }
        json shouldEqual """{"ages":{"start":18,"end":40},"distance":{"start":5,"end":50}}"""

        val actual = mapper.readValue<ClassWithRanges>(json)
        actual shouldEqual expected
    }

    data class ClassWithPairMixedNullableTypes(val person: Pair<String?, Int?>)

    @Test
    fun `bind pair mixed nullable types`() {
        val expected = ClassWithPairMixedNullableTypes(Pair("John", null))
        val json = mapper.writeValueAsString(expected)

        logger.trace { "json=$json" }
        json shouldEqual """{"person":{"first":"John","second":null}}"""

        val actual = mapper.readValue<ClassWithPairMixedNullableTypes>(json)
        actual shouldEqual expected
    }

    data class GenericParametersClass<A, B: Any>(val one: A, val two: B)
    data class GenericParameterConsumer(val thing: GenericParametersClass<String?, Int>)

    @Test
    fun `generic parameters in constructor`() {
        val expected = GenericParameterConsumer(GenericParametersClass(null, 123))
        val json = mapper.writeValueAsString(expected)

        logger.trace { "json=$json" }
        json shouldEqual """{"thing":{"one":null,"two":123}}"""

        val actual = mapper.readValue<GenericParameterConsumer>(json)
        actual shouldEqual expected
    }

    data class TinyPerson(val name: String, val age: Int)
    class KotlinPersonIterator(val people: List<TinyPerson>): Iterator<TinyPerson> by people.iterator()

    @Test
    fun `generate iterator to json`() {
        val expected = KotlinPersonIterator(listOf(TinyPerson("Fred", 10), TinyPerson("Max", 11)))
        val typeRef = jacksonTypeRef<Iterator<TinyPerson>>()
        val json = mapper.writerFor(typeRef).writeValueAsString(expected)

        logger.trace { "json=$json" }
        json shouldEqual """[{"name":"Fred","age":10},{"name":"Max","age":11}]"""

        val actual = mapper.readValue<List<TinyPerson>>(json)
        actual.shouldNotBeNull()
        actual shouldContainAll expected.people
    }

    class Company(val name: String,
                  @JsonSerialize(`as` = java.util.Iterator::class)
                  val people: KotlinPersonIterator)

    @Test
    fun `generate iterator as field`() {
        val people = KotlinPersonIterator(listOf(TinyPerson("Fred", 10), TinyPerson("Max", 11)))
        val company = Company("KidVille", people)

        val json = mapper.writeValueAsString(company)
        logger.trace { "json=$json" }
        json shouldEqual """{"name":"KidVille","people":[{"name":"Fred","age":10},{"name":"Max","age":11}]}"""

        // NOTE: Iterator를 읽어드릴 수는 없네 ...
        //        val actual = mapper.readValue<Company>(json)
        //        actual shouldEqual company
    }
}