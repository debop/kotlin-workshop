package io.github.debop

import io.github.debop.controller.BackendAController
import io.github.debop.controller.BackendBController
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
abstract class AbstractResilience4jTest {

    companion object {
        const val BACKEND_A = BackendAController.ROOT_PATH
        const val BACKEND_B = BackendBController.ROOT_PATH
    }

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    protected fun procedureFailure(backend: String) {
        val response = restTemplate.getForEntity<String>("/$backend/failure")
        response.statusCode shouldEqual HttpStatus.INTERNAL_SERVER_ERROR
    }

    protected fun procedureSuccess(backend: String) {
        val response = restTemplate.getForEntity<String>("/$backend/success")
        response.statusCode shouldEqual HttpStatus.OK
    }

    protected fun procedureMonoFailure(backend: String) {
        val response = restTemplate.getForEntity<String>("/$backend/monoFailure")
        response.statusCode shouldEqual HttpStatus.INTERNAL_SERVER_ERROR
    }

    protected fun procedureMonoSuccess(backend: String) {
        val response = restTemplate.getForEntity<String>("/$backend/monoSuccess")
        response.statusCode shouldEqual HttpStatus.OK
    }

    protected fun checkRetryStatus(kind: String, backend: String, count: String) {
        val metricsResponse = restTemplate.getForEntity<String>("/actuator/prometheus")
        metricsResponse.body.shouldNotBeNull()
        val response = metricsResponse.body!!
        response shouldContain "resilience4j_retry_calls{application=\"resilience4j-demo\",kind=\"$kind\",name=\"$backend\",} $count"
    }

}