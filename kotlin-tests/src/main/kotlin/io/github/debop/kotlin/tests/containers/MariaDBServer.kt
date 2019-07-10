package io.github.debop.kotlin.tests.containers

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import mu.KLogging
import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait

/**
 * MariaDBServer
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 5
 */
class MariaDBServer(tag: String = DEFAULT_TAG,
                    useDefaultPort: Boolean = false,
                    configuration: String = "",
                    username: String = "test",
                    password: String = "test"): MariaDBContainer<MariaDBServer>("$IMAGE:$tag") {

    companion object: KLogging() {
        const val MARIADB_PORT = 3306
    }

    val host: String by lazy { containerIpAddress }
    val port: Int by lazy { getMappedPort(MARIADB_PORT) }

    init {
        if (configuration.isNotBlank()) {
            withConfigurationOverride(configuration)
        }
        withUsername(username)
        withPassword(password)

        setWaitStrategy(Wait.forListeningPort())
        withLogConsumer(Slf4jLogConsumer(logger))

        if (useDefaultPort) {
            withCreateContainerCmdModifier {
                it.withPortBindings(PortBinding(Ports.Binding.bindPort(MARIADB_PORT), ExposedPort(MARIADB_PORT)))
            }
        }

        start()
    }

    override fun start() {
        super.start()

        setSystemProperties(logger, "mariadb", "Start MariaDB Server", host, port)
    }
}