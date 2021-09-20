package io.github.debop.idgenerators.ksuid

import mu.KLogging
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * KsuidTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 20
 */
class KsuidTest {

    companion object: KLogging()

    @Test
    fun `Generate Ksuid`() {
        val ksuid = Ksuid.generate()

        logger.debug { "Generated Ksuid=$ksuid" }
        logger.debug { Ksuid.prettyString(ksuid) }
    }

    @Disabled("KSUID 생성 시 정렬이 완벽하지 않다. 같은 Timestamp라면 Sequence 값을 추가해주어야 한다")
    @Test
    fun `K-Sortable Unique ID 정렬이 완벽하지는 않다`() {
        val ids = List(100) { Ksuid.generate() }
        val sorted = ids.sorted()

        repeat(100) {
            sorted[it] shouldBeEqualTo ids[it]
        }
    }
}