package io.github.debop.kotlin.tests.extensions

import mu.KLogging
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotBeNullOrEmpty
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.util.stream.Stream

@TestInstance(Lifecycle.PER_CLASS)
@Randomized
class RandomizedParameterTest {

    companion object : KLogging() {
        const val TEST_COUNT = 20
    }

    private val anyStrings = HashSet<String>()
    private val anyNumbers = HashSet<Int>()

    @RepeatedTest(TEST_COUNT)
    fun `can inject a random string`(@Random anyString: String) {
        anyString.shouldNotBeNullOrEmpty()
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject a random string and numbers`(@Random anyString: String,
                                                 @Random anyInt: Int,
                                                 @Random anyDouble: Double) {
        anyString.shouldNotBeNullOrEmpty()

        anyInt.shouldNotBeNull()
        anyDouble.shouldNotBeNull()

        logger.debug { "anyString=$anyString" }
        logger.debug { "anyInt=$anyInt, anyDouble=$anyDouble" }
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject a fully populated random object`(@Random domainObject: DomainObject) {
        domainObject.shouldFullyPopulated()
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject a partially populated random object`(
        @Random(excludes = ["wotsits", "id", "nestedDomainObject.address"])
        domainObject: DomainObject
    ) {
        domainObject.shouldPartiallyPopulated()
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject a random list of default size`(@Random(type = String::class) anyList: List<String>) {
        anyList.shouldNotBeNull()
        anyList.shouldNotBeEmpty()
        anyList.size shouldEqualTo getDefaultSizeOfRandom()
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject a ramdom set`(@Random(type = String::class) anySet: Set<String>) {
        anySet.shouldNotBeNull()
        anySet.shouldNotBeEmpty()
        anySet.size shouldEqualTo getDefaultSizeOfRandom()
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject a random stream`(@Random(type = String::class) anyStream: Stream<String>) {
        anyStream.shouldNotBeNull()
        anyStream.count() shouldEqualTo getDefaultSizeOfRandom().toLong()
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject a ramdom collection`(@Random(type = String::class) anyCollection: Collection<String>) {
        anyCollection.shouldNotBeNull()
        anyCollection.shouldNotBeEmpty()
        anyCollection.size shouldEqualTo getDefaultSizeOfRandom()
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject random fully populated domain objects`(
        @Random(size = 2, type = DomainObject::class) anyFullyPopulatedDomainObjects: List<DomainObject>) {
        anyFullyPopulatedDomainObjects.shouldNotBeNull()
        anyFullyPopulatedDomainObjects.shouldNotBeEmpty()
        anyFullyPopulatedDomainObjects.size shouldEqualTo 2
        anyFullyPopulatedDomainObjects.forEach {
            it.shouldFullyPopulated()
        }
    }

    @RepeatedTest(TEST_COUNT)
    fun `can inject random partially populated domain objects`(
        @Random(size = 2, type = DomainObject::class, excludes = ["wotsits", "id", "nestedDomainObject.address"])
        anyPartiallyPopulatedDomainObjects: List<DomainObject>) {

        anyPartiallyPopulatedDomainObjects.shouldNotBeNull()
        anyPartiallyPopulatedDomainObjects.shouldNotBeEmpty()
        anyPartiallyPopulatedDomainObjects.size shouldEqualTo 2
        anyPartiallyPopulatedDomainObjects.forEach {
            it.shouldPartiallyPopulated()
        }
    }

    @RepeatedTest(TEST_COUNT)
    fun `will inject a new random values each time`(@Random anyString: String,
                                                    @Random anyNumber: Int) {

        anyStrings.shouldNotContain(anyString)
        anyStrings.add(anyString)

        anyNumbers.shouldNotContain(anyNumber)
        anyNumbers.add(anyNumber)
    }

}