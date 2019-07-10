dependencies {

    api(project(":kotlin-basic"))
    testImplementation(project(":kotlin-tests"))

    api(Libraries.kotlinx_coroutines_jdk8)

    api(Libraries.javers_core)
    api(Libraries.javers_spring)
    api(Libraries.javers_spring_jpa)
    api(Libraries.javers_spring_mongo)
    api(Libraries.javers_persistence_mongo)
    api(Libraries.javers_persistence_sql)
    api(Libraries.javers_spring_boot_starter_sql)
}