plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

repositories {
    maven { setUrl("https://oss.jfrog.org/artifactory/oss-snapshot-local/") }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

dependencies {

    api(project(":kotlin-coroutines"))
    testApi(project(":kotlin-tests"))

    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.boot:spring-boot-starter-aop")

    api("org.springframework.boot:spring-boot-autoconfigure")
    implementation("org.springframework.boot:spring-boot-configuration-processor")

    api(Libraries.kotlinx_coroutines_jdk8)
    api(Libraries.kotlinx_coroutines_reactor)

    api(Libraries.resilience4j_all)
    api(Libraries.resilience4j_kotlin)
    api(Libraries.resilience4j_spring_boot2)
    api(Libraries.resilience4j_reactor)

    api(Libraries.jackson_module_kotlin)
    api(Libraries.micrometer_registry_prometheus)

    api(Libraries.vavr_jackson)

    testApi(Libraries.reactor_test)
    testApi("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}