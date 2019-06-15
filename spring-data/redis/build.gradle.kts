plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    testImplementation(project(":kotlin-tests"))

    api("org.springframework.boot:spring-boot-starter-data-redis")

    api(Libraries.netty_transport_native_epoll)
    api(Libraries.netty_transport_native_kqueue)

    implementation(Libraries.fst)
    implementation(Libraries.kryo)
    implementation(Libraries.kryo_serializers)

    implementation(Libraries.latencyUtils)
    implementation(Libraries.hdrHistogram)

    implementation(Libraries.jackson_module_kotlin)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}