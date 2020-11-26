package io.github.debop.rsocket.server

import mu.KLogging
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RSocketServerApplicationTest {

    companion object: KLogging()

    @Test
    fun `context loading`() {
        logger.info { "Application tested." }
    }
}