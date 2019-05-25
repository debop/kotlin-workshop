import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

    implementation("io.github.microutils:kotlin-logging:1.6.26")
    implementation("org.slf4j:slf4j-api:1.7.25")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("net.jodah:failsafe:${extra["failsafe"]}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra.get("coroutines")}")

    val junitJupiterVersion = extra["junitJupiter"]
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")

    testImplementation("org.amshove.kluent:kluent:1.45")
}