plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":spring-data:redis"))
    testCompile(project(":kotlin-tests"))

    api("org.springframework.boot:spring-boot-starter-data-redis")

    implementation(Libraries.fst)
    implementation(Libraries.kryo)
    implementation(Libraries.kryo_serializers)

    implementation(Libraries.netty_transport_native_epoll)
    implementation(Libraries.netty_transport_native_kqueue)

    implementation(Libraries.latencyUtils)
    implementation(Libraries.hdrHistogram)

    implementation(Libraries.jackson_module_kotlin)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}