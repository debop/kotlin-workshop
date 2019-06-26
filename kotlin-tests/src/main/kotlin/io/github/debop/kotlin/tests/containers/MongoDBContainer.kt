package io.github.debop.kotlin.tests.containers

import com.github.dockerjava.api.model.ExposedPort
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.Ports.Binding
import mu.KLogging
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait

/**
 * TestContainers 를 이용하여 필요 시에만 MongoDB를 docker로 실행할 수 있도록 해 줍니다.
 * @author debop (Sunghyouk Bae)
 *
 * @param dockerImageName MongoDB docker image name
 * @param useDefaultPort mongodb 기본 port 를 이용할 것인가 여부. 기본적으로는 docker의 dynamic port를 이용한다
 */
class MongoDBContainer(dockerImageName: String,
                       useDefaultPort: Boolean = false) : GenericContainer<MongoDBContainer>(dockerImageName) {

    companion object : KLogging() {
        const val IMAGE_NAME: String = "mongo"
        const val DEFAULT_TAG: String = "4.0.10"
        const val MONGODB_PORT: Int = 27017

        val Instance: MongoDBContainer by lazy { createMongoDBContainer() }

        fun createMongoDBContainer(tag: String = DEFAULT_TAG, useDefaultPort: Boolean = false): MongoDBContainer {
            return MongoDBContainer("$IMAGE_NAME:$tag", useDefaultPort)
        }
    }

    val host: String get() = containerIpAddress
    val port: Int get() = getMappedPort(MONGODB_PORT)

    val connectionString: String
        get() = "mongodb://$host:$port"

    init {
        withExposedPorts(MONGODB_PORT)
        withLogConsumer(Slf4jLogConsumer(logger))
        setWaitStrategy(Wait.forListeningPort())

        if (useDefaultPort) {
            withCreateContainerCmdModifier {
                it.withPortBindings(PortBinding(Binding.bindPort(MONGODB_PORT), ExposedPort(MONGODB_PORT)))
            }
        }
        start()
    }

    override fun start() {
        super.start()

        // spring boot 환경설정에서 이 값을 참조하여 사용하면 된다.
        System.setProperty("testcontainers.mongodb.host", host)
        System.setProperty("testcontainers.mongodb.port", port.toString())
        System.setProperty("testcontainers.mongodb.connectionString", connectionString)

        logger.info {
            """
            |Start TestContainer MongoDB:
            |    testcontainers.monogodb.host=$host
            |    testcontainers.mongodb.port=$port
            |    testcontainers.mongodb.connectionString=$connectionString
            """.trimMargin()
        }
    }
}