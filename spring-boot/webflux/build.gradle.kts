plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
    id("org.springframework.boot") version ("2.1.5.RELEASE")
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }

    dependencies {
        dependency("org.jetbrains.kotlin:kotlin-stdlib:${extra["kotlin"]}")
        dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${extra["kotlin"]}")
        dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${extra["kotlin"]}")
        dependency("org.jetbrains.kotlin:kotlin-reflect:${extra["kotlin"]}")
        dependency("org.jetbrains.kotlin:kotlin-test:${extra["kotlin"]}")
    }
}

dependencies {

    implementation(project(":kotlin-coroutines"))
    implementation(project(":kotlin-tests"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra.get("coroutines")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${extra.get("coroutines")}")

    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("net.jodah:failsafe:${extra["failsafe"]}")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("io.projectreactor:reactor-test")

    implementation("com.atlassian.commonmark:commonmark:0.12.1")
    implementation("com.atlassian.commonmark:commonmark-ext-autolink:0.12.1")
}