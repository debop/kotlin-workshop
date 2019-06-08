package io.github.debop

import mu.KotlinLogging.logger
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.boot.actuate.health.Status
import org.springframework.boot.test.web.client.getForEntity

/**
 * CircuitBreakerTest
 * @author debop (Sunghyouk Bae)
 */
@Suppress("UNCHECKED_CAST")
class CircuitBreakerTest : AbstractResilience4jTest() {

    private val log = logger {}

    @Test
    fun `open and close circuit breaker for backendA`() {
        // backendA: circuit breaker ringbuffer = 5
        repeat(5) { procedureFailure(BACKEND_A) }

        checkHealthStatus(BACKEND_A + "CircuitBreaker", Status.DOWN)

        // backendA: circuit breaker waitDurationInOpenStateMillis = 2000
        Thread.sleep(2500L)

        repeat(3) { procedureSuccess(BACKEND_A) }

        checkHealthStatus(BACKEND_A + "CircuitBreaker", Status.UP)
    }

    @Test
    fun `open and close circuit breaker for backendB`() {
        // backendA: circuit breaker ringbuffer = 10
        repeat(10) { procedureFailure(BACKEND_B) }

        checkHealthStatus(BACKEND_B + "CircuitBreaker", Status.UNKNOWN)

        // backendA: circuit breaker waitDurationInOpenStateMillis = 1000
        Thread.sleep(1500L)

        repeat(3) { procedureSuccess(BACKEND_B) }

        checkHealthStatus(BACKEND_B + "CircuitBreaker", Status.UP)
    }

    private fun checkHealthStatus(circuitBreakerName: String, status: Status) {
        val healthResponse = restTemplate.getForEntity<HealthResponse>("/actuator/health")

        val body = healthResponse.body
        body.shouldNotBeNull()
        body.details.shouldNotBeNull()

        val circuitBreakerDetails = body.details[circuitBreakerName] as Map<String, Any?>

        circuitBreakerDetails.shouldNotBeNull()
        circuitBreakerDetails["status"] shouldEqual status.toString()
    }
}