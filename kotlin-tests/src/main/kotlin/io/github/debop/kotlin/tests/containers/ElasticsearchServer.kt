package io.github.debop.kotlin.tests.containers

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports.Binding
import mu.KLogging
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.elasticsearch.ElasticsearchContainer

/**
 * ElasticSearchServer
 * @author debop (Sunghyouk Bae)
 */
class ElasticsearchServer(
    tag: String = DEFAULT_TAG,
    useDefaultPort: Boolean = false,
): ElasticsearchContainer("$IMAGE:$tag") {

    companion object: KLogging() {
        const val IMAGE = "docker.elastic.co/elasticsearch/elasticsearch"
        const val DEFAULT_TAG = "7.9.2"
        const val NAME = "elasticsearch"

        const val ELASTICSEARCH_PORT = 9200
        const val ELASTICSEARCH_TCP_PORT = 9300
    }

    init {
        withExposedPorts(ELASTICSEARCH_PORT, ELASTICSEARCH_TCP_PORT)
        withLogConsumer(Slf4jLogConsumer(logger))
        setWaitStrategy(Wait.forListeningPort())

        // BUG: 왜 기본 Port 매핑이 안되는지???
        if (useDefaultPort) {
            withCreateContainerCmdModifier {
                it.withPortBindings(
                    PortBinding(Binding.bindPort(ELASTICSEARCH_PORT), ExposedPort(ELASTICSEARCH_PORT)),
                    PortBinding(Binding.bindPort(ELASTICSEARCH_TCP_PORT), ExposedPort(ELASTICSEARCH_TCP_PORT))
                )
            }
        }
    }

    // val host: String by lazy { containerIpAddress }
    val port: Int by lazy { getMappedPort(ELASTICSEARCH_PORT) }
    val tcpPort: Int by lazy { getMappedPort(ELASTICSEARCH_TCP_PORT) }
    val url: String get() = "http://$host:$port"

    override fun start() {
        super.start()

        val name = "$CONTAINER_PREFIX.$NAME"

        System.setProperty("$name.host", host)
        System.setProperty("$name.port", port.toString())
        System.setProperty("$name.tcp-port", tcpPort.toString())
        System.setProperty("$name.url", url)

        RedisServer.logger.info {
            """
            |Start TestContainer Elasticsearch:
            |    $name.host=$host
            |    $name.port=$port
            |    $name.tcp-port=$tcpPort
            |    $name.url=$url
            """.trimMargin()
        }
    }
}