package io.github.debop.kotlin.tests.containers

import mu.KLogging
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ElasticsearchServerTest {

    companion object: KLogging() {
        val elasticsearchServer = ElasticsearchServer(useDefaultPort = true)
    }

    @BeforeAll
    fun beforeAll() {
        elasticsearchServer.start()
    }

    @AfterAll
    fun afterAll() {
        elasticsearchServer.stop()
    }

    @Test
    fun `run elasticsearch server`() {
        elasticsearchServer.isRunning.shouldBeTrue()

        // BUG: 왜 기본 Port 매핑이 안되는지???
        elasticsearchServer.port shouldEqualTo ElasticsearchServer.ELASTICSEARCH_PORT
        elasticsearchServer.tcpPort shouldEqualTo ElasticsearchServer.ELASTICSEARCH_TCP_PORT
    }
}