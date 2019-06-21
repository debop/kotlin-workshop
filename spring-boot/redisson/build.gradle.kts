plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":kotlin-coroutines"))
    implementation(project(":kotlin-tests"))

    implementation(Libraries.redisson_spring_boot_starter)
    implementation(Libraries.redisson_spring_data_21)

    implementation(Libraries.jackson_module_kotlin)

    // NOTE: linux-x86_64 를 따로 추가해줘야 제대로 classifier가 지정된다. 이유는 모르겠지만, 이렇게 해야 제대로 된 jar를 참조한다
    testImplementation(Libraries.netty_transport_native_epoll + ":linux-x86_64")
    testImplementation(Libraries.netty_transport_native_kqueue + ":osx-x86_64")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}