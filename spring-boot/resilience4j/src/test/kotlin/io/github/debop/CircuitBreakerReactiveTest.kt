package io.github.debop

import mu.KotlinLogging
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.health.Status
import org.springframework.test.web.reactive.server.WebTestClient

/**
 * CircuitBreakerReactiveTest
 * @author debop (Sunghyouk Bae)
 */
@Suppress("UNCHECKED_CAST")
class CircuitBreakerReactiveTest : AbstractResilience4jTest() {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `open and close circuit breaker for backendA`() {
        // backendA: circuit breaker ringbuffer = 5
        repeat(5) { procedureMonoFailure(BACKEND_A) }

        checkHealthStatus(BACKEND_A + "CircuitBreaker", Status.DOWN)

        // backendA: circuit breaker waitDurationInOpenStateMillis = 2000
        Thread.sleep(2500L)

        repeat(3) { procedureMonoSuccess(BACKEND_A) }

        checkHealthStatus(BACKEND_A + "CircuitBreaker", Status.UP)
    }

    @Test
    fun `open and close circuit breaker for backendB`() {
        // backendA: circuit breaker ringbuffer = 10
        repeat(10) { procedureMonoFailure(BACKEND_B) }

        checkHealthStatus(BACKEND_B + "CircuitBreaker", Status.UNKNOWN)

        // backendA: circuit breaker waitDurationInOpenStateMillis = 1000
        Thread.sleep(1500L)

        repeat(3) { procedureMonoSuccess(BACKEND_B) }

        checkHealthStatus(BACKEND_B + "CircuitBreaker", Status.UP)
    }

    override fun procedureMonoFailure(backend: String) {
        webClient.get().uri("/$backend/monoFailure")
            .exchange()
            .expectStatus()
            .is5xxServerError
    }

    override fun procedureMonoSuccess(backend: String) {
        webClient.get().uri("/$backend/monoSuccess")
            .exchange()
            .expectStatus()
            .isOk
    }

    private fun checkHealthStatus(circuitBreakerName: String, status: Status) {
        val healthResponse = webClient.get().uri("/actuator/health")
            .exchange()
            .expectBody(HealthResponse::class.java)
            .returnResult()

        val body = healthResponse.responseBody
        body.shouldNotBeNull()
        body.details.shouldNotBeNull()

        log.debug { "health details=${body.details}" }

        val circuitBreakerDetails = body.details[circuitBreakerName] as? Map<String, Any?>

        circuitBreakerDetails.shouldNotBeNull()
        circuitBreakerDetails["status"] shouldBeEqualTo status.toString()
    }
}