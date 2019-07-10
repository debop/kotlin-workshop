package io.github.debop

import mu.KotlinLogging.logger
import org.junit.jupiter.api.Test

class RetryTest : AbstractResilience4jTest() {

    private val log = logger {}

    @Test
    fun `should retry three times`() {
        procedureFailure(BACKEND_A)

        checkRetryStatus("failed_with_retry", BACKEND_A, "1.0")
    }

    @Test
    fun `should successed without retry`() {
        procedureSuccess(BACKEND_A)

        checkRetryStatus("successful_without_retry", BACKEND_A, "1.0")
    }
}