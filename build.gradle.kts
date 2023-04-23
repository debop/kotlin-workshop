import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

plugins {
    base
    `maven-publish`
    jacoco
    kotlin("jvm") version Versions.kotlin

    // see: https://kotlinlang.org/docs/reference/compiler-plugins.html
    kotlin("plugin.spring") version Versions.kotlin apply false
    kotlin("plugin.allopen") version Versions.kotlin apply false
    kotlin("plugin.noarg") version Versions.kotlin apply false
    kotlin("plugin.jpa") version Versions.kotlin apply false
    kotlin("plugin.serialization") version Versions.kotlin apply false
    // kotlin("plugin.atomicfu") version Versions.kotlin apply false
    kotlin("kapt") version Versions.kotlin apply false

    //id(BuildPlugins.detekt) version BuildPlugins.Versions.detekt apply false
    id(BuildPlugins.dokka) version BuildPlugins.Versions.dokka apply false
    id(BuildPlugins.dependency_management) version BuildPlugins.Versions.dependency_management
    id(BuildPlugins.spring_boot) version BuildPlugins.Versions.spring_boot apply false

}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}

subprojects {

    apply {
        plugin<JavaLibraryPlugin>()
        plugin<KotlinPlatformJvmPlugin>()

        plugin("jacoco")
        plugin("maven-publish")

        // plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jetbrains.dokka")
        plugin("io.spring.dependency-management")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    val javaVersion = JavaVersion.VERSION_17.toString()

    tasks {
        compileJava {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        compileKotlin {
            kotlinOptions {
                jvmTarget = javaVersion
                languageVersion = "1.8"
                apiVersion = "1.8"
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-Xinline-classes",
                    "-Xallow-result-return-type",
                    "-Xstring-concat=indy",         // since Kotlin 1.4.20 for JVM 9+
                    "-progressive",                 // since Kotlin 1.6
                    "-Xenable-builder-inference",   // since Kotlin 1.6
                    "-Xbackend-threads=0",          // since 1.6.20 (0 means one thread per CPU core)
                    // "-Xuse-k2"                   // since Kotlin 1.7  // kapt not support
                )

                val experimentalAnnotations = listOf(
                    "kotlin.RequiresOptIn",
                    "kotlin.contracts.ExperimentalContracts",
                    "kotlin.experimental.ExperimentalTypeInference",
                    "kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "kotlinx.coroutines.InternalCoroutinesApi",
                    "kotlinx.coroutines.FlowPreview",
                )
                freeCompilerArgs = freeCompilerArgs.plus(experimentalAnnotations.map { "-opt-in=$it" })
            }
        }

        compileTestKotlin {
            kotlinOptions {
                jvmTarget = javaVersion
                languageVersion = "1.8"
                apiVersion = "1.8"
                freeCompilerArgs = listOf(
                    "-Xjsr305=strict",
                    "-Xjvm-default=all",
                    "-Xinline-classes",
                    "-Xallow-result-return-type",
                    "-Xstring-concat=indy",         // since Kotlin 1.4.20 for JVM 9+
                    "-progressive",                 // since Kotlin 1.6
                    "-Xenable-builder-inference",   // since Kotlin 1.6
                    "-Xbackend-threads=0",          // since 1.6.20 (0 means one thread per CPU core)
                    // "-Xuse-k2"                      // since Kotlin 1.7 // kapt not support
                )

                val experimentalAnnotations = listOf(
                    "kotlin.RequiresOptIn",
                    "kotlin.Experimental",
                    "kotlin.ExperimentalStdlibApi",
                    "kotlin.time.ExperimentalTime",
                    "kotlin.contracts.ExperimentalContracts",
                    // "kotlin.experimental.ExperimentalTypeInference",
                    "kotlinx.coroutines.ExperimentalCoroutinesApi",
                    "kotlinx.coroutines.InternalCoroutinesApi",
                    "kotlinx.coroutines.FlowPreview",
                )
                freeCompilerArgs = freeCompilerArgs.plus(experimentalAnnotations.map { "-opt-in=$it" })
            }
        }
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
            events("failed")
        }
    }

    //    detekt {
    //        description = "Runs a failfast detekt build."
    //
    //        input = files("src/main/kotlin")
    //        config = files("${project.rootProject.rootDir}/detekt.yml")
    //        filters = ".*/build/.*"
    //
    //        reports {
    //            xml.enabled = false
    //            html.enabled = true
    //        }
    //    }

    // jacoco
    configure<JacocoPluginExtension> {
    }

    tasks.withType<JacocoReport> {
        reports {
            html.required.set(true)
            xml.required.set(true)
            csv.required.set(false)
        }
    }

    tasks["clean"].doLast {
        delete("./.project")
        delete("./out")
        delete("./bin")
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.spring_boot}")
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${Versions.spring_cloud}")
            mavenBom(Libraries.kotlin_bom)
        }
        dependencies {
            dependency(Libraries.kotlin_stdlib)
            dependency(Libraries.kotlin_stdlib_jdk7)
            dependency(Libraries.kotlin_stdlib_jdk8)
            dependency(Libraries.kotlin_reflect)
            dependency(Libraries.kotlin_test)
            dependency(Libraries.kotlin_test_junit5)

            dependency(Libraries.kotlinx_coroutines_core)
            dependency(Libraries.kotlinx_coroutines_jdk7)
            dependency(Libraries.kotlinx_coroutines_jdk8)
            dependency(Libraries.kotlinx_coroutines_reactor)
            dependency(Libraries.kotlinx_coroutines_rx2)

            // Apache Commons
            dependency(Libraries.commons_collections4)
            dependency(Libraries.commons_lang3)
            dependency(Libraries.commons_compress)
            dependency(Libraries.commons_codec)
            dependency(Libraries.commons_csv)
            dependency(Libraries.commons_math3)
            dependency(Libraries.commons_pool2)
            dependency(Libraries.commons_text)
            dependency(Libraries.commons_exec)
            dependency(Libraries.commons_io)

            dependency(Libraries.kotlin_logging)
            dependency(Libraries.slf4j_api)
            dependency(Libraries.logback)

            dependency(Libraries.findbugs)
            dependency(Libraries.guava)
            dependency(Libraries.joda_time)

            dependency(Libraries.fst)
            dependency(Libraries.kryo)
            dependency(Libraries.kryo_serializers)

            // Jackson
            dependency(Libraries.jackson_annotations)
            dependency(Libraries.jackson_core)
            dependency(Libraries.jackson_databind)
            dependency(Libraries.jackson_datatype_jsr310)
            dependency(Libraries.jackson_datatype_jdk8)
            dependency(Libraries.jackson_datatype_joda)
            dependency(Libraries.jackson_datatype_guava)

            dependency(Libraries.jackson_module_paranamer)
            dependency(Libraries.jackson_module_parameter_names)
            dependency(Libraries.jackson_module_kotlin)
            dependency(Libraries.jackson_module_afterburner)

            // Reactor
            dependency(Libraries.reactor_core)
            dependency(Libraries.reactor_kotlin_extensions)
            dependency(Libraries.reactor_netty)
            dependency(Libraries.reactor_test)

            dependency(Libraries.hikaricp)
            dependency(Libraries.mysql_connector_java)
            dependency(Libraries.mariadb_java_client)
            dependency(Libraries.h2)

            // Mongo Driver
            dependency(Libraries.mongo_bson)
            dependency(Libraries.mongo_driver_core)
            dependency(Libraries.mongo_driver_sync)
            dependency(Libraries.mongo_driver_reactivestreams)

            dependency(Libraries.byte_buddy)
            dependency(Libraries.byte_buddy_agent)

            dependency(Libraries.objenesis)
            dependency(Libraries.ow2_asm)

            dependency(Libraries.junit_jupiter)
            dependency(Libraries.junit_jupiter_api)
            dependency(Libraries.junit_jupiter_engine)
            dependency(Libraries.junit_jupiter_params)

            dependency(Libraries.junit_platform_commons)
            dependency(Libraries.junit_platform_engine)

            dependency(Libraries.kluent)
            dependency(Libraries.assertj_core)

            dependency(Libraries.mockk)
            dependency(Libraries.mockito_core)
            dependency(Libraries.mockito_junit_jupiter)
            dependency(Libraries.mockito_kotlin)

            dependency(Libraries.testcontainers)
        }
    }

    dependencies {
        val api by configurations
        val implementation by configurations
        val testImplementation by configurations
        val testRuntimeOnly by configurations

        implementation(Libraries.kotlin_stdlib)
        implementation(Libraries.kotlin_stdlib_jdk8)
        implementation(Libraries.kotlin_reflect)
        testImplementation(Libraries.kotlin_test)
        testImplementation(Libraries.kotlin_test_junit5)

        implementation(Libraries.kotlinx_coroutines_jdk8)
        // implementation(Libraries.atomicfu)

        api(Libraries.commons_lang3)

        api(Libraries.kotlin_logging)
        testImplementation(Libraries.logback)

        testImplementation(Libraries.junit_jupiter)
        testRuntimeOnly(Libraries.junit_platform_engine)

        testImplementation(Libraries.kluent)
        testImplementation(Libraries.assertj_core)

        testImplementation(Libraries.testcontainers)
    }
}

//dependencies {
//    // Make the root project archives configuration depend on every subproject
//    subprojects.forEach {
//        archives(it)
//    }
//}
