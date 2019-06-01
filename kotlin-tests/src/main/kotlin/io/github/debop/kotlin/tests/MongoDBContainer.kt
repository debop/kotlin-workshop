package io.github.debop.kotlin.tests

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports
import mu.KLogging
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait

/**
 * MongoDBContainer
 * @author debop (Sunghyouk Bae)
 */
class MongoDBContainer(dockerImageName: String) : GenericContainer<MongoDBContainer>(dockerImageName) {

    val host: String get() = containerIpAddress
    val port: Int get() = getMappedPort(EXPOSED_PORT)

    val connectionString: String
        get() = "mongodb://$host:$port"

    init {
        logger.info { "Create MongoDBContainer ... " }
        withExposedPorts(EXPOSED_PORT)
        withCreateContainerCmdModifier { cmd ->
            cmd.withPortBindings(PortBinding(Ports.Binding.bindPort(EXPOSED_PORT), ExposedPort(EXPOSED_PORT)))
        }
        withLogConsumer(Slf4jLogConsumer(logger))
        setWaitStrategy(Wait.forListeningPort())

        start()

        logger.info { "MongoDBContainer started!!! connectionString=$connectionString" }
    }

    companion object : KLogging() {
        const val IMAGE_NAME: String = "mongo"
        const val DEFAULT_TAG: String = "4.0.10"
        const val EXPOSED_PORT: Int = 27017

        val instance: MongoDBContainer by lazy { createMongoDBContainer() }

        fun createMongoDBContainer(tag: String = DEFAULT_TAG): MongoDBContainer {
            return MongoDBContainer("$IMAGE_NAME:$tag")
        }
    }
}