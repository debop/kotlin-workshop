package io.github.debop.kotlin.tests.containers

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import mu.KLogging
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer

/**
 * PostgreSQLServer
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 5
 */
class PostgreSQLServer(tag: String = DEFAULT_TAG,
                       useDefaultPort: Boolean = false,
                       username: String = "test",
                       password: String = "test"): PostgreSQLContainer<PostgreSQLServer>("$IMAGE:$tag") {

    companion object: KLogging() {
        const val POSTGRESQL_10_TAG = "10.6"
        const val POSTGRESQL_11_TAG = "11.1"
    }

    // val host: String by lazy { containerIpAddress }
    val port: Int by lazy { getMappedPort(POSTGRESQL_PORT) }

    init {
        withUsername(username)
        withPassword(password)

        withLogConsumer(Slf4jLogConsumer(logger))

        // PostgreSQL은 Wait 관련은 할 필요가 없다
        // setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            withCreateContainerCmdModifier {
                it.withPortBindings(PortBinding(Ports.Binding.bindPort(POSTGRESQL_PORT), ExposedPort(POSTGRESQL_PORT)))
            }
        }

        start()
    }

    override fun start() {
        super.start()

        setSystemProperties(logger, "postgresql", "Start PostgreSQL Server", host, port)
    }
}