import io.gitlab.arturbosch.detekt.detekt
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.31"

    base
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.noarg") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false

    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC12" apply false
    id("org.jetbrains.dokka") version "0.9.17" apply false
    id("io.spring.dependency-management") version "1.0.6.RELEASE" apply false
}

allprojects {

    group = "io.github.debop"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
    }
}

subprojects {

    apply {
        plugin<JavaLibraryPlugin>()
        plugin<KotlinPlatformJvmPlugin>()

        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jetbrains.dokka")

        plugin("jacoco")
        plugin("maven-publish")
        plugin("io.spring.dependency-management")
    }


    tasks.withType<KotlinCompile> {
        sourceCompatibility = "1.8"
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    the<DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${extra["spring.boot2"]}")
        }
        dependencies {
            dependency("org.jetbrains.kotlin:kotlin-stdlib:${extra["kotlin"]}")
            dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${extra["kotlin"]}")
            dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${extra["kotlin"]}")
            dependency("org.jetbrains.kotlin:kotlin-reflect:${extra["kotlin"]}")
            dependency("org.jetbrains.kotlin:kotlin-test:${extra["kotlin"]}")

            dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra["coroutines"]}")
            dependency("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${extra["coroutines"]}")

            dependency("net.jodah:failsafe:${extra["failsafe"]}")

            dependency("io.github.microutils:kotlin-logging:1.6.26")
            dependency("ch.qos.logback:logback-classic:1.2.3")

            dependency("org.junit.jupiter:junit-jupiter:${extra["junit.jupiter"]}")
            dependency("org.amshove.kluent:kluent:1.45")

            dependency("org.testcontainers:testcontainers:${extra["testcontainers"]}")
        }
    }

    dependencies {
        val compile by configurations
        val testCompile by configurations
        val implementation by configurations
        val testImplementation by configurations

        implementation(kotlin("stdlib-jdk8"))
        implementation(kotlin("reflect"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

        implementation("org.apache.commons:commons-lang3")
        implementation("net.jodah:failsafe")

        implementation("io.github.microutils:kotlin-logging")
        testImplementation("ch.qos.logback:logback-classic")

        testImplementation("org.junit.jupiter:junit-jupiter")
        testImplementation("org.amshove.kluent:kluent")
    }

    val sourceSets = project.the<SourceSetContainer>()

    val sourcesJar by tasks.creating(Jar::class) {
        from(sourceSets["main"].allSource)
        archiveClassifier.set("sources")
    }

    // Configure existing Dokka task to output HTML to typical Javadoc directory
    val dokka by tasks.getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
    }

    // Create dokka Jar task from dokka task output
    val dokkaJar by tasks.creating(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        description = "Assembles Kotlin docs with Dokka"
        archiveClassifier.set("javadoc")
        // dependsOn(tasks.dokka) not needed; dependency automatically inferred by from(tasks.dokka)
        from(dokka)
    }

    tasks.withType<Test> {
        useJUnitPlatform()

        testLogging {
            events("FAILED")
        }

        maxParallelForks = Runtime.getRuntime().availableProcessors()
        setForkEvery(1L)
    }

    detekt {
        description = "Runs a failfast detekt build."

        input = files("src/main/kotlin")
        config = files("${project.rootProject.rootDir}/detekt.yml")
        filters = ".*/build/.*"

        reports {
            xml.enabled = false
            html.enabled = true
        }
    }

    // jacoco
    configure<JacocoPluginExtension> {
    }

    tasks.withType<JacocoReport> {
        reports {
            html.isEnabled = true
            xml.isEnabled = true
            csv.isEnabled = false
        }
    }

    tasks["clean"].doLast {
        delete("./.project")
        delete("./out")
        delete("./bin")
    }
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
