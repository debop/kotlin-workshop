import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
}

kotlin {
    KotlinPlatformType.jvm
}
tasks.withType<KotlinCompile> {
    sourceCompatibility = "1.8"
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra["coroutines"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${extra.get("coroutines")}")

    compile("org.apache.commons:commons-lang3:3.9")

    compile("io.github.microutils:kotlin-logging:1.6.26")
    compile("org.slf4j:slf4j-api:1.7.25")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:${extra["spring.boot2"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${extra["spring.boot2"]}")

    testImplementation("com.h2database:h2:1.4.199")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${extra["junit.jupiter"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${extra["junit.jupiter"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${extra["junit.jupiter"]}")

    testImplementation("org.amshove.kluent:kluent:1.45")
}