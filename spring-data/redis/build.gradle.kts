plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    api(project(":kotlin-tests"))
    api("org.springframework.boot:spring-boot-starter-data-redis")

    // NOTE: linux-x86_64 를 따로 추가해줘야 제대로 classifier가 지정된다. 이유는 모르겠지만, 이렇게 해야 제대로 된 jar를 참조한다
    api(Libraries.netty_transport_native_epoll + ":linux-x86_64")
    api(Libraries.netty_transport_native_kqueue + ":osx-x86_64")

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