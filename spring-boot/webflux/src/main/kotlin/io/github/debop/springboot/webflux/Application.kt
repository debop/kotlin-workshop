package io.github.debop.springboot.webflux

import com.mongodb.ConnectionString
import com.mongodb.connection.netty.NettyStreamFactoryFactory
import io.github.debop.kotlin.tests.containers.MongoDBServer
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    companion object {
        val mongodb = MongoDBServer()
    }

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
    runApplication<Application>(*args) {
        webApplicationType = WebApplicationType.REACTIVE
        setBannerMode(Banner.Mode.CONSOLE)
    }
}