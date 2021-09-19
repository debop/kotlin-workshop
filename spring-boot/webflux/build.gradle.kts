import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")

    id(BuildPlugins.spring_boot)
}

val bootJar: BootJar by tasks
bootJar.enabled = false

dependencies {

    implementation(project(":kotlin-coroutines"))
    implementation(project(":kotlin-tests"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }

    implementation("com.atlassian.commonmark:commonmark:0.12.1")
    implementation("com.atlassian.commonmark:commonmark-ext-autolink:0.12.1")
    implementation(Libraries.jackson_module_kotlin)

    implementation(Libraries.kotlinx_coroutines_jdk8)
    implementation(Libraries.kotlinx_coroutines_reactor)

    implementation(Libraries.mongo_driver_sync)
    implementation(Libraries.mongo_driver_reactivestreams)

    implementation(Libraries.reactor_core)
    implementation(Libraries.reactor_kotlin_extensions)
    testImplementation(Libraries.reactor_test)
}