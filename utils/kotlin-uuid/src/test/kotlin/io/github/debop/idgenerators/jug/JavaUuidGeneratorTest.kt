package io.github.debop.idgenerators.jug

import com.fasterxml.uuid.EthernetAddress
import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.UUIDComparator
import mu.KLogging
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import kotlin.streams.toList

/**
 * JavaUuidGeneratorTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 20
 */
class JavaUuidGeneratorTest {

    companion object: KLogging() {
        const val TEST_COUNT = 4096 * 4
        val TEST_LIST = (0 until TEST_COUNT).toList()
    }

    val timeBasedGen = Generators.timeBasedGenerator(EthernetAddress.fromInterface())

    @Test
    fun `generate timebased uuid`() {
        val u1 = timeBasedGen.generate()
        val u2 = timeBasedGen.generate()
        val u3 = timeBasedGen.generate()

        UUIDComparator.staticCompare(u1, u2) shouldBeLessThan 0
        UUIDComparator.staticCompare(u2, u3) shouldBeLessThan 0

        logger.debug { "u1=$u1" }
        logger.debug { "u2=$u2" }
        logger.debug { "u3=$u3" }
    }

    @RepeatedTest(5)
    fun `generate timebased uuid as parallel`() {
        val uids = TEST_LIST.parallelStream().map { timeBasedGen.generate() }.toList()

        uids.toSet().size shouldEqualTo uids.size
    }
}