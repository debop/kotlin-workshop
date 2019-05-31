plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
    // id("org.springframework.boot") version ("2.1.5.RELEASE")
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${extra["spring.boot2"]}")
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
    testImplementation(project(":kotlin-tests"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra.get("coroutines")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${extra.get("coroutines")}")

    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("net.jodah:failsafe:${extra["failsafe"]}")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:${extra["spring.boot2"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${extra["spring.boot2"]}")

    testImplementation("com.h2database:h2:1.4.199")
}