plugins {
    kotlin("plugin.spring")
    id(BuildPlugins.spring_boot)
}

dependencies {

    api(project(":rsocket:rsocket-communication:api"))

    api(Libraries.kotlinx_coroutines_jdk8)
    api(Libraries.kotlinx_coroutines_reactor)

    api(Libraries.reactor_core)
    api(Libraries.reactor_netty)

    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springframework.boot:spring-boot-starter-rsocket")
    api("org.springframework.boot:spring-boot-starter-webflux")

    testApi("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testApi(Libraries.reactor_test)
}