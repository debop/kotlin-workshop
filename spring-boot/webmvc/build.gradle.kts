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
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }

    implementation("com.atlassian.commonmark:commonmark:0.12.1")
    implementation("com.atlassian.commonmark:commonmark-ext-autolink:0.12.1")
    implementation(Libraries.jackson_module_kotlin)

    implementation(Libraries.mongo_driver_sync)
    implementation(Libraries.netty_all)
}