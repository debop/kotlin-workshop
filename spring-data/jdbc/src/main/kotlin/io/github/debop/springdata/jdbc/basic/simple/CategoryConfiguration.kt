package io.github.debop.springdata.jdbc.basic.simple

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.data.jdbc.repository.config.JdbcConfiguration
import org.springframework.data.relational.core.mapping.event.BeforeSaveEvent
import org.springframework.data.relational.core.mapping.event.RelationalEvent

/**
 * CategoryConfiguration
 * @author debop (Sunghyouk Bae)
 */
@Configuration
@EnableJdbcRepositories
@Import(JdbcConfiguration::class)
class CategoryConfiguration {

    @Bean
    fun loggingListener(): ApplicationListener<ApplicationEvent> {
        return ApplicationListener { event ->
            if (event is RelationalEvent) {
                println("Received an event: $event")
            }
        }
    }

    @Bean
    fun timeStampingSaveTime(): ApplicationListener<BeforeSaveEvent> {
        return ApplicationListener { event ->
            val entity = event.entity
            if (entity is Category) {
                entity.timeStamp()
            }
        }
    }
}