package io.github.debop

import mu.KotlinLogging.logger
import org.junit.jupiter.api.Test

/**
 * RetryReactiveTest
 * @author debop (Sunghyouk Bae)
 */

class RetryReactiveTest : AbstractResilience4jTest() {

    private val log = logger {}

    @Test
    fun `should retry three times`() {
        procedureMonoFailure(BACKEND_A)

        checkRetryStatus("failed_with_retry", BACKEND_A, "1.0")
    }

    @Test
    fun `should successed without retry`() {
        procedureMonoSuccess(BACKEND_A)

        checkRetryStatus("successful_without_retry", BACKEND_A, "1.0")
    }

}