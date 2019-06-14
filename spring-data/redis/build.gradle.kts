plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":kotlin-tests"))
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    api("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("io.netty:netty-transport-native-epoll")
    implementation("io.netty:netty-transport-native-kqueue")

    implementation("org.latencyutils:LatencyUtils")
    implementation("org.hdrhistogram:HdrHistogram")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}