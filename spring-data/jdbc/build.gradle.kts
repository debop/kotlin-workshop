import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.31"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.3.31"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.3.31"
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

    implementation(kotlin("stdlib"))

    compile("org.apache.commons:commons-lang3:3.9")

    compile("io.github.microutils:kotlin-logging:1.6.26")
    compile("org.slf4j:slf4j-api:1.7.25")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")


    implementation("org.springframework.data:spring-data-jdbc:1.0.8.RELEASE")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.1.5.RELEASE")

    testImplementation("com.h2database:h2:1.4.199")

    val junitJupiterVersion = extra["junitJupiter"] as String
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")

    testImplementation("org.amshove.kluent:kluent:1.45")
}