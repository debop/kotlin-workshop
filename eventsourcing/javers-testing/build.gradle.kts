plugins {
    idea
    kotlin("plugin.spring")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
    kotlin("kapt")
}

dependencies {

    api(project(":eventsourcing:javers-kotlin"))
    testCompile(project(":kotlin-tests"))

    api(Libraries.javers_core)
    api(Libraries.javers_spring)
    api(Libraries.javers_spring_jpa)
    api(Libraries.javers_spring_mongo)
    api(Libraries.javers_persistence_mongo)
    api(Libraries.javers_persistence_sql)
    api(Libraries.javers_spring_boot_starter_sql)

    api(Libraries.jackson_module_kotlin)
    api(Libraries.cache2k_core)

    testCompile(Libraries.h2)
    testCompile(Libraries.hikaricp)

    implementation("org.springframework.data:spring-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}