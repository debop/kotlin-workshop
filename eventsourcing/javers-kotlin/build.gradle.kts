plugins {
    id("com.github.hauner.jarTest") version "1.0.1"
}

dependencies {
    api(Libraries.javers_core)
    api(Libraries.javers_spring)
    api(Libraries.javers_spring_jpa)
    api(Libraries.javers_spring_mongo)
    api(Libraries.javers_persistence_mongo)
    api(Libraries.javers_persistence_sql)
    api(Libraries.javers_spring_boot_starter_sql)

    api(Libraries.jackson_module_kotlin)
    api(Libraries.cache2k_core)
}