plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
}

dependencies {

    implementation(project(":kotlin-coroutines"))
    implementation(project(":kotlin-tests"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }

    implementation("com.atlassian.commonmark:commonmark:0.12.1")
    implementation("com.atlassian.commonmark:commonmark-ext-autolink:0.12.1")
}