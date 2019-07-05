package io.github.debop.kotlin.tests.containers

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import mu.KLogging
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait


/**
 * MySQLServer
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 5
 */
class MySQLServer(tag: String = DEFAULT_TAG,
                  useDefaultPort: Boolean = false,
                  configuration: String = "",
                  username: String = "test",
                  password: String = "test"): MySQLContainer<MySQLServer>("$IMAGE:$tag") {

    companion object: KLogging() {
        const val MYSQL_8_TAG = "8.0"
        const val DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver"
    }

    val host: String by lazy { containerIpAddress }
    val port: Int by lazy { getMappedPort(MYSQL_PORT) }

    init {
        if(configuration.isNotBlank()) {
            withConfigurationOverride(configuration)
        }
        withUsername(username)
        withPassword(password)

        setWaitStrategy(Wait.forListeningPort())
        withLogConsumer(Slf4jLogConsumer(logger))

        if(useDefaultPort) {
            withCreateContainerCmdModifier {
                it.withPortBindings(PortBinding(Ports.Binding.bindPort(MYSQL_PORT), ExposedPort(MYSQL_PORT)))
            }
        }

        start()
    }

    override fun getDriverClassName(): String = DRIVER_CLASS_NAME

    override fun start() {
        super.start()

        setSystemProperties(logger, "mysql", "Start MySQL Server", host, port)
    }
}