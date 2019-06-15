plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":kotlin-tests"))

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation(Libraries.jackson_module_kotlin)
    implementation(Libraries.cache2k_spring)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}