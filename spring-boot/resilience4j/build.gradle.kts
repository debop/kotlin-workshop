plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

repositories {
    maven { setUrl("http://oss.jfrog.org/artifactory/oss-snapshot-local/") }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

ext {
    extra["resilience4j"] = "0.16.0-SNAPSHOT"
}

dependencies {

    val resilience4j: String by extra

    api(project(":kotlin-coroutines"))
    testApi(project(":kotlin-tests"))

    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-aop")

    api("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    api("io.github.resilience4j:resilience4j-spring-boot2:$resilience4j")
    api("io.github.resilience4j:resilience4j-reactor:$resilience4j")

    implementation("io.micrometer:micrometer-registry-prometheus")

    testApi("io.projectreactor:reactor-test")
    testApi("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}