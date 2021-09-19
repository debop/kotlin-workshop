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
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(Libraries.querydsl_jpa)
    // NOTE: classifier를 지정할 때에는 추가적으로 ":classfier" 를 지정해주어야 한다.
    // Libraries 에 미리 정의하면 인식하지 못한다
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