package io.github.debop.example.cache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.scheduling.annotation.EnableScheduling


@EnableScheduling
@SpringBootApplication
class SampleCacheApplication


fun main() {
    SpringApplicationBuilder()
        .sources(SampleCacheApplication::class.java)
        .profiles("app")
        .run()
}