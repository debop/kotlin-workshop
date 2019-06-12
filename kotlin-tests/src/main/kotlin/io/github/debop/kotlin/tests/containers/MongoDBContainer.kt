package io.github.debop.kotlin.tests.containers

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

        // mongodb 의 기본 port를 mapping 하는 방식
        // 이 것은 mongodb 를 공유할 경우 문제가 된다. start() 에 있는 것 처럼 환경설정으로 제공하는 방식이 좋다
        //        withCreateContainerCmdModifier { cmd ->
        //            cmd.withPortBindings(PortBinding(Ports.Binding.bindPort(EXPOSED_PORT), ExposedPort(EXPOSED_PORT)))
        //        }
        withLogConsumer(Slf4jLogConsumer(logger))
        setWaitStrategy(Wait.forListeningPort())

        start()

        logger.info { "MongoDBContainer started!!! connectionString=$connectionString" }
    }

    override fun start() {
        super.start()

        // spring boot 환경설정에서 이 값을 참조하여 사용하면 된다.
        System.setProperty("testcontainers.mongodb.host", host)
        System.setProperty("testcontainers.mongodb.port", port.toString())
        System.setProperty("testcontainers.mongodb.connectionString", connectionString)

        logger.info {
            """Start TestContainer MongoDB:
            |    testcontainers.monogodb.host=$host
            |    testcontainers.mongodb.port=$port
            |    testcontainers.mongodb.connectionString=$connectionString
            """.trimMargin()
        }
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