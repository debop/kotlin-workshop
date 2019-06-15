plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":kotlin-tests"))

    implementation(Libraries.jackson_module_kotlin)

    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}