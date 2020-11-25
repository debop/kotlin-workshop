plugins {
    kotlin("plugin.spring")
    id(BuildPlugins.spring_boot)
}

dependencies {

    api(project(":rsocket:rsocket-communication:api"))

    api(Libraries.kotlinx_coroutines_jdk8)
    api(Libraries.kotlinx_coroutines_reactor)

    api("org.springframework.boot:spring-boot-starter-rsocket")

    testApi("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testApi(Libraries.reactor_test)

    // 이건 Deprecated 된 것입니다. 다른 걸 쓰시길 바랍니다.
    api("org.springframework.shell:spring-shell-starter:2.0.1.RELEASE")
}