package io.github.debop.springboot.routes

import com.mongodb.ConnectionString
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import io.github.debop.kotlin.tests.containers.MongoDBContainer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    val mongodb = MongoDBContainer.instance

    @Bean
    fun clientSettingsBuilderCustomizer() =
        MongoClientSettingsBuilderCustomizer { builder ->
            builder.streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
            builder.applyToServerSettings {
                it.applyConnectionString(ConnectionString(mongodb.connectionString))
            }
        }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}