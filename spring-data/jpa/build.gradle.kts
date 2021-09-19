plugins {
    idea
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
    kotlin("kapt")
}

dependencies {

    implementation(project(":kotlin-coroutines"))
    testImplementation(project(":kotlin-tests"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(Libraries.querydsl_jpa)
    kapt(Libraries.querydsl_apt + ":jpa")
    kaptTest(Libraries.querydsl_apt + ":jpa")

    testImplementation(Libraries.hikaricp)
    testImplementation(Libraries.h2)
}

idea {
    module {
        sourceDirs.plus(file("build/generated/source/kapt/main"))
        generatedSourceDirs.plus(file("build/generated/source/kapt/main"))
        testSourceDirs.plus(file("build/generated/source/kapt/test"))
    }
}