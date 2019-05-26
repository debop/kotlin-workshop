import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

kotlin {
    KotlinPlatformType.jvm
}
tasks.withType<KotlinCompile> {
    sourceCompatibility = "1.8"
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra.get("coroutines")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${extra.get("coroutines")}")

    compile("org.apache.commons:commons-lang3:3.9")

    implementation("io.github.microutils:kotlin-logging:1.6.26")
    implementation("org.slf4j:slf4j-api:1.7.25")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("net.jodah:failsafe:${extra["failsafe"]}")


    testImplementation("org.junit.jupiter:junit-jupiter-api:${extra["junit.jupiter"]}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${extra["junit.jupiter"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${extra["junit.jupiter"]}")

    testImplementation("org.amshove.kluent:kluent:1.45")
}