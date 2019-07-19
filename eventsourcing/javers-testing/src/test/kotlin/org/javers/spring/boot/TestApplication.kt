package org.javers.spring.boot

import org.javers.spring.auditable.CommitPropertiesProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("org.javers.spring.boot.sql", "org.javers.spring.boot.repository")
class TestApplication {

    @Bean
    fun commitPropertiesProvider(): CommitPropertiesProvider =
        CommitPropertiesProvider {
            mapOf(
                "key" to "ok",
                "system" to "wms"
            )
        }
}

fun main() {
    runApplication<TestApplication>()
}

