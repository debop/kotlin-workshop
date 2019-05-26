import io.gitlab.arturbosch.detekt.detekt
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    val kotlinVersion: String = "1.3.31"
    base
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.noarg") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false

    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC12" apply false
    id("org.jetbrains.dokka") version "0.9.17" apply false
}

val kotlinVersion: String by extra { plugins.getPlugin(KotlinPluginWrapper::class.java).kotlinPluginVersion }

allprojects {

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
