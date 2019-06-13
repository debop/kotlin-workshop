package io.github.debop.example.cache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

@EnableCaching
@EnableScheduling
@SpringBootApplication
class SampleJCacheApplication


fun main() {
    SpringApplicationBuilder()
        .sources(SampleJCacheApplication::class.java)
        .profiles("app")
        .run()
}