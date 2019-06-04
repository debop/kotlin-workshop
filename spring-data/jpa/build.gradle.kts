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

    compile("com.querydsl:querydsl-jpa:4.2.1")
    kapt("com.querydsl:querydsl-apt:4.2.1:jpa")
    kaptTest("com.querydsl:querydsl-apt:4.2.1:jpa")

    testImplementation("com.h2database:h2:1.4.199")
}

idea {
    module {
        sourceDirs.plus(file("build/generated/source/kapt/main"))
        generatedSourceDirs.plus(file("build/generated/source/kapt/main"))
        testSourceDirs.plus(file("build/generated/source/kapt/test"))
    }
}