package io.github.debop.idgenerators.uuidcreator

import com.github.f4b6a3.uuid.UuidCreator
import com.github.f4b6a3.uuid.factory.LexicalOrderGuidCreator
import com.github.f4b6a3.uuid.factory.SequentialUuidCreator
import com.github.f4b6a3.uuid.factory.TimeBasedUuidCreator
import mu.KLogging
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.RepeatedTest
import kotlin.streams.toList

/**
 * UuidCreatorExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 20
 */
class UuidCreatorExample {

    companion object: KLogging() {
        const val TEST_COUNT = 4096 * 4
        val TEST_LIST = (0 until TEST_COUNT).toList()
    }

    val timeBasedCreator: TimeBasedUuidCreator = UuidCreator.getTimeBasedCreator()
    val timeBasedWithMac: TimeBasedUuidCreator = UuidCreator.getTimeBasedCreator().withHardwareAddressNodeIdentifier()
    val sequentialCreator: SequentialUuidCreator = UuidCreator.getSequentialCreator()
    val sequentialWithMac: SequentialUuidCreator = UuidCreator.getSequentialCreator().withHardwareAddressNodeIdentifier()

    val lexicalOrderCreator: LexicalOrderGuidCreator = UuidCreator.getLexicalOrderCreator()

    @RepeatedTest(5)
    fun `get timebased uuid`() {
        val ids = TEST_LIST.parallelStream().map { timeBasedCreator.create() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }

    @RepeatedTest(5)
    fun `get timebased uuid with hardware address`() {
        val ids = TEST_LIST.parallelStream().map { timeBasedWithMac.create() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }

    @RepeatedTest(5)
    fun `get sequence uuid`() {
        val ids = TEST_LIST.parallelStream().map { sequentialCreator.create() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }

    @RepeatedTest(5)
    fun `get sequence uuid with hardware address`() {
        val ids = TEST_LIST.parallelStream().map { sequentialWithMac.create() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }

    @RepeatedTest(5)
    fun `get lexical ordered guid`() {
        val ids = TEST_LIST.parallelStream().map { lexicalOrderCreator.create() }.toList()
        ids.toSet().size shouldEqualTo ids.size
    }
}