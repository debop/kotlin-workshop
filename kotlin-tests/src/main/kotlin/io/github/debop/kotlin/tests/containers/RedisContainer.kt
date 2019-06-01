package io.github.debop.kotlin.tests.containers

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports.Binding
import mu.KLogging
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait

/**
 * TestContainers 를 이용하여 필요 시에만 Redis를 docker로 실행할 수 있도록 해 줍니다.
 * @author debop (Sunghyouk Bae)
 */
class RedisContainer(dockerImageName: String) : GenericContainer<RedisContainer>(dockerImageName) {

    companion object : KLogging() {
        const val IMAGE_NAME: String = "redis"
        const val DEFAULT_TAG: String = "5.0.5"
        const val EXPOSED_PORT: Int = 6379

        val instance: RedisContainer by lazy { create() }

        fun create(tag: String = DEFAULT_TAG): RedisContainer {
            return RedisContainer("$IMAGE_NAME:$tag")
        }
    }

    val host: String get() = containerIpAddress
    val port: Int get() = getMappedPort(EXPOSED_PORT)
    val url: String get() = "redis://$host:$port"

    init {
        logger.info { "Create RedisContainer..." }

        withExposedPorts(EXPOSED_PORT)
        withCreateContainerCmdModifier { cmd ->
            cmd.withPortBindings(PortBinding(Binding.bindPort(EXPOSED_PORT), ExposedPort(EXPOSED_PORT)))
        }
        withLogConsumer(Slf4jLogConsumer(logger))
        setWaitStrategy(Wait.forListeningPort())

        start()

        logger.info { "RedisContainer started!!! url=$url" }
    }
}