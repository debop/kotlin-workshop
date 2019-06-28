import io.gitlab.arturbosch.detekt.detekt
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {

    base
    kotlin("jvm") version Versions.kotlin apply false

    // see: https://kotlinlang.org/docs/reference/compiler-plugins.html
    kotlin("plugin.spring") version Versions.kotlin apply false
    kotlin("plugin.allopen") version Versions.kotlin apply false
    kotlin("plugin.noarg") version Versions.kotlin apply false
    kotlin("plugin.jpa") version Versions.kotlin apply false
    // Use old scripts
    // kotlin("plugin.sam-with-receiver") version kotlinVersion apply false

    id(BuildPlugins.detekt) version BuildPlugins.Versions.detekt apply false
    id(BuildPlugins.dokka) version BuildPlugins.Versions.dokka apply false
    id(BuildPlugins.dependency_management) version BuildPlugins.Versions.dependencyManagement
    `maven-publish`
}

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

        plugin("jacoco")
        plugin("maven-publish")

        plugin("io.gitlab.arturbosch.detekt")
        plugin("org.jetbrains.dokka")
        plugin("io.spring.dependency-management")
        plugin("maven-publish")
    }

    tasks.withType<KotlinCompile> {
        sourceCompatibility = "1.8"
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
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
            events("FAILED")
        }
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

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:${Versions.spring_boot}")
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

            // Resilience4j
            dependency(Libraries.resilience4j_annotations)
            dependency(Libraries.resilience4j_bulkhead)
            dependency(Libraries.resilience4j_circuitbreaker)
            dependency(Libraries.resilience4j_core)
            dependency(Libraries.resilience4j_framework_common)
            dependency(Libraries.resilience4j_micrometer)
            dependency(Libraries.resilience4j_ratelimiter)
            dependency(Libraries.resilience4j_reactor)
            dependency(Libraries.resilience4j_retry)
            dependency(Libraries.resilience4j_spring)
            dependency(Libraries.resilience4j_spring_boot2)
            dependency(Libraries.resilience4j_spring_boot_common)

            // Vavr
            dependency(Libraries.vavr)
            dependency(Libraries.vavr_jackson)
            dependency(Libraries.vavr_kotlin)
            dependency(Libraries.vavr_match)
            dependency(Libraries.vavr_test)

            // Netty
            dependency(Libraries.netty_all)
            dependency(Libraries.netty_common)
            dependency(Libraries.netty_buffer)
            dependency(Libraries.netty_codec)
            dependency(Libraries.netty_codec_dns)
            dependency(Libraries.netty_codec_http)
            dependency(Libraries.netty_codec_http2)
            dependency(Libraries.netty_codec_socks)
            dependency(Libraries.netty_handler)
            dependency(Libraries.netty_handler_proxy)
            dependency(Libraries.netty_resolver)
            dependency(Libraries.netty_resolver_dns)
            dependency(Libraries.netty_transport)
            dependency(Libraries.netty_transport_native_epoll)
            dependency(Libraries.netty_transport_native_kqueue)

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

            // Micrometer
            dependency(Libraries.micrometer_core)
            dependency(Libraries.micrometer_test)
            dependency(Libraries.micrometer_registry)
            dependency(Libraries.micrometer_registry_prometheus)
            dependency(Libraries.micrometer_registry_graphite)
            dependency(Libraries.micrometer_registry_jmx)

            // Reactor
            dependency(Libraries.reactor_core)
            dependency(Libraries.reactor_test)
            dependency(Libraries.reactor_netty)

            dependency(Libraries.rxjava2)

            dependency(Libraries.mongo_java_driver)
            dependency(Libraries.mongo_bson)
            dependency(Libraries.mongo_driver)
            dependency(Libraries.mongo_driver_async)
            dependency(Libraries.mongo_driver_core)
            dependency(Libraries.mongo_driver_reactivestreams)

            // Hibernate
            dependency(Libraries.hibernate_core)
            dependency(Libraries.hibernate_jpa_2_1_api)
            dependency(Libraries.javassist)
            dependency(Libraries.querydsl_apt)
            dependency(Libraries.querydsl_jpa)

            // Validators
            dependency(Libraries.validation_api)
            dependency(Libraries.hibernate_validator)
            dependency(Libraries.hibernate_validator_annotation_processor)
            dependency(Libraries.javax_el_api)
            dependency(Libraries.javax_el)

            dependency(Libraries.hikaricp)
            dependency(Libraries.mysqlConnectorJava)
            dependency(Libraries.mariadbJavaClient)
            dependency(Libraries.h2)

            // Cache
            dependency(Libraries.cache_api)
            dependency(Libraries.cache2kAll)
            dependency(Libraries.cache2k_spring)
            dependency(Libraries.cache2k_jcache)

            // Dagger
            dependency(Libraries.dagger)
            dependency(Libraries.dagger_compiler)

            // Koin
            dependency(Libraries.koin_core)
            dependency(Libraries.koin_core_ext)
            dependency(Libraries.koin_test)

            // Metrics
            dependency(Libraries.latencyUtils)
            dependency(Libraries.hdrHistogram)

            dependency(Libraries.byte_buddy)
            dependency(Libraries.byte_buddy_agent)

            dependency(Libraries.objenesis)
            dependency(Libraries.ow2_asm)

            dependency(Libraries.random_beans)
            dependency(Libraries.reflectasm)

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
        val compile by configurations
        val implementation by configurations
        val testImplementation by configurations
        val testRuntimeOnly by configurations

        implementation(Libraries.kotlin_stdlib_jdk8)
        implementation(Libraries.kotlin_reflect)
        testImplementation(Libraries.kotlin_test)
        testImplementation(Libraries.kotlin_test_junit5)

        implementation(Libraries.kotlinx_coroutines_jdk8)

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

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
