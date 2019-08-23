package io.github.debop.kotlin.tests.containers

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports.Binding
import org.testcontainers.containers.output.Slf4jLogConsumer

/**
 * CockroachDBServer
 * @author debop (Sunghyouk Bae)
 */
class CockroachDBServer(tag: String = CockroachDBContainer.DEFAULT_TAG,
                        useDefaultPort: Boolean = false,
                        username: String = "root",
                        password: String = ""): CockroachDBContainer<CockroachDBServer>("$IMAGE:$tag") {

    val host: String by lazy { containerIpAddress }
    val port: Int by lazy { getMappedPort(COCKROACH_PORT) }

    init {
        withUsername(username)
        withPassword(password)

        withLogConsumer(Slf4jLogConsumer(logger))

        if (useDefaultPort) {
            withCreateContainerCmdModifier {
                val port = PortBinding(Binding.bindPort(COCKROACH_PORT), ExposedPort(COCKROACH_PORT))
                val apiPort = PortBinding(Binding.bindPort(REST_API_PORT), ExposedPort(REST_API_PORT))
                it.withPortBindings(port, apiPort)
            }
        }

        start()
    }

    override fun start() {
        super.start()

        setSystemProperties(logger, NAME, "Start CockroachDB Server", host, port)
    }
}