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
 *
 * @param tag            Redis docker's tag
 * @param useDefaultPort Redis 기본 port 를 이용할 것인가 여부. 기본적으로는 docker의 dynamic port를 이용한다
 */
class RedisServer(tag: String = REDIS_TAG,
                  useDefaultPort: Boolean = false): GenericContainer<RedisServer>("$REDIS_IMAGE_NAME:$tag") {

    companion object: KLogging() {
        const val REDIS_IMAGE_NAME: String = "redis"
        const val REDIS_TAG: String = "5.0.5"
        const val REDIS_PORT: Int = 6379
    }

    val host: String get() = containerIpAddress
    val port: Int get() = getMappedPort(REDIS_PORT)
    val url: String get() = "redis://$host:$port"

    init {
        withExposedPorts(REDIS_PORT)
        withLogConsumer(Slf4jLogConsumer(logger))
        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            withCreateContainerCmdModifier {
                it.withPortBindings(PortBinding(Binding.bindPort(REDIS_PORT), ExposedPort(REDIS_PORT)))
            }
        }

        start()
    }

    override fun start() {
        super.start()

        val name = "testcontainers.redis"

        System.setProperty("$name.host", host)
        System.setProperty("$name.port", port.toString())
        System.setProperty("$name.url", url)

        logger.info {
            """
            |Start TestContainer Redis:
            |    $name.host=$host
            |    $name.port=$port
            |    $name.url=$url
            """.trimMargin()
        }
    }
}