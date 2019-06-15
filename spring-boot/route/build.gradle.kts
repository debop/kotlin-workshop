plugins {
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
}

dependencies {

    implementation(project(":kotlin-coroutines"))
    testImplementation(project(":kotlin-tests"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }

    implementation("com.atlassian.commonmark:commonmark:0.12.1")
    implementation("com.atlassian.commonmark:commonmark-ext-autolink:0.12.1")
    implementation(Libraries.jackson_module_kotlin)

    testImplementation(Libraries.reactor_test)
}