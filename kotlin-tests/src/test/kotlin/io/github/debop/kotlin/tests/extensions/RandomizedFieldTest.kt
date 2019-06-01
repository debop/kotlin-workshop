package io.github.debop.kotlin.tests.extensions

import mu.KLogging
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotBeNullOrEmpty
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.stream.Stream

@TestInstance(Lifecycle.PER_METHOD)
@Randomized
class RandomizedFieldTest {

    companion object : KLogging() {
        const val TEST_COUNT = 20
    }

    val anyStrings = ConcurrentLinkedQueue<String>()

    // @Random 사용 시 private는 지원하지 않습니다.

    @Random
    lateinit var anyString: String

    @Random
    lateinit var fullyPopulatedDomainObject: DomainObject

    @Random(excludes = ["wotsits", "id", "nestedDomainObject.address"])
    lateinit var partiallyPopulatedDomainObject: DomainObject

    @Random(type = String::class)
    lateinit var anyList: List<String>

    @Random(size = 5, type = String::class)
    lateinit var anyListOfSpecificSize: List<String>

    @Random(type = String::class)
    lateinit var anySet: Set<String>

    @Random(type = String::class)
    lateinit var anyStream: Stream<String>

    @Random(type = String::class)
    lateinit var anyCollection: Collection<String>

    @Random(size = 2, type = DomainObject::class)
    lateinit var anyFullyPopulatedDomainObjects: List<DomainObject>

    @Random(size = 2, type = DomainObject::class, excludes = ["wotsits", "id", "nestedDomainObject.address"])
    lateinit var anyPartiallyPopulatedDomainObject: List<DomainObject>

    @Test
    fun `can onject a random string`() {
        anyString.shouldNotBeNullOrEmpty()
    }

    @Test
    fun `can inject a fullly populated random object`() {
        fullyPopulatedDomainObject.shouldFullyPopulated()
    }

    @Test
    fun `can inject a partially populated random object`() {
        partiallyPopulatedDomainObject.shouldPartiallyPopulated()
    }

    @Test
    fun `can inject a random list of default size`() {
        anyList.shouldNotBeNull()
        anyList.shouldNotBeEmpty()
        anyList.size shouldEqualTo getDefaultSizeOfRandom()
    }

    @Test
    fun `can inject a random list of specific size`() {
        anyListOfSpecificSize.shouldNotBeNull()
        anyListOfSpecificSize.shouldNotBeEmpty()
        anyListOfSpecificSize.size shouldEqualTo 5
    }

    @Test
    fun `can inject a random set`() {
        anySet.shouldNotBeNull()
        anySet.shouldNotBeEmpty()
        anySet.size shouldEqualTo getDefaultSizeOfRandom()
    }

    @Test
    fun `can inject a random stream`() {
        anyStream.shouldNotBeNull()
        anyStream.count() shouldEqualTo getDefaultSizeOfRandom().toLong()
    }

    @Test
    fun `can inject a random collection`() {
        anyCollection.shouldNotBeNull()
        anyCollection.shouldNotBeEmpty()
        anyCollection.size shouldEqualTo getDefaultSizeOfRandom()
    }

    @Test
    fun `can inject random fully populated domain object`() {
        anyFullyPopulatedDomainObjects.shouldNotBeNull()
        anyFullyPopulatedDomainObjects.shouldNotBeEmpty()
        anyFullyPopulatedDomainObjects.forEach {
            it.shouldFullyPopulated()
        }
    }

    @Test
    fun `can inject random partially populated domain object`() {
        anyPartiallyPopulatedDomainObject.shouldNotBeNull()
        anyPartiallyPopulatedDomainObject.shouldNotBeEmpty()
        anyPartiallyPopulatedDomainObject.forEach {
            it.shouldPartiallyPopulated()
        }
    }

    // JUnit 5의 Parallel mode 에서는 field 값은 한번에 정해지므로, 같은 값을 가지게 된다.
    // 이럴 때를 대비해 parameter로 제공하는 것이 가장 안전한 방식이다.
    @RepeatedTest(TEST_COUNT)
    fun `will inject a new random value each time`() {
        anyString.shouldNotBeNullOrEmpty()

        if (anyStrings.isEmpty()) {
            anyStrings.add(anyString)
        } else {
            anyStrings.shouldNotContain(anyString)
            anyStrings.add(anyString)
        }
    }
}