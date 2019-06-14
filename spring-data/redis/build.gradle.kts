plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":kotlin-tests"))

    api("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("de.ruedigermoeller:fst:2.57")
    implementation("com.esotericsoftware:kryo:4.0.2")
    implementation("de.javakaffee:kryo-serializers:0.45")

    implementation("io.netty:netty-transport-native-epoll")
    implementation("io.netty:netty-transport-native-kqueue")

    implementation("org.latencyutils:LatencyUtils")
    implementation("org.hdrhistogram:HdrHistogram")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}