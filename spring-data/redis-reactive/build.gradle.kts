plugins {
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
}

noArg {
    annotation("io.github.debop.kotlin.workshop.annotation.KotlinValueClass")
}

dependencies {

    implementation(project(":kotlin-basic"))
    implementation(project(":spring-data:redis"))

    api(project(":kotlin-tests"))
    api("org.springframework.boot:spring-boot-starter-data-redis")

    api(Libraries.netty_transport_native_epoll)
    api(Libraries.netty_transport_native_kqueue)

    implementation(Libraries.fst)
    implementation(Libraries.kryo)
    implementation(Libraries.kryo_serializers)

    implementation(Libraries.latencyUtils)
    implementation(Libraries.hdrHistogram)

    implementation(Libraries.jackson_module_kotlin)

    testImplementation(Libraries.reactor_test)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}