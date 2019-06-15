plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":kotlin-coroutines"))
    testImplementation(project(":kotlin-tests"))

    implementation(Libraries.redisson_spring_boot_starter)
    implementation(Libraries.redisson_spring_data_21)

    implementation(Libraries.jackson_module_kotlin)

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
}