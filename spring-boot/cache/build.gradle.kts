plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":kotlin-tests"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.cache2k:cache2k-spring:1.2.2.Final")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}