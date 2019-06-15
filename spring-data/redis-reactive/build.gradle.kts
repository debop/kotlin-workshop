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
    implementation(project(":kotlin-tests"))

    api("org.springframework.boot:spring-boot-starter-data-redis")
    api("io.netty:netty-transport-native-epoll")
    api("io.netty:netty-transport-native-kqueue")

    implementation("de.ruedigermoeller:fst:2.57")
    implementation("com.esotericsoftware:kryo:4.0.2")
    implementation("de.javakaffee:kryo-serializers:0.45")

    implementation("org.latencyutils:LatencyUtils")
    implementation("org.hdrhistogram:HdrHistogram")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }

    testImplementation("io.projectreactor:reactor-test")
}