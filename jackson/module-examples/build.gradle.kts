
dependencies {

    api(project(":kotlin-basic"))

    api(Libraries.jackson_databind)

    api(Libraries.jackson_datatype_jdk8)
    api(Libraries.jackson_datatype_jsr310)

    api(Libraries.jackson_module_kotlin)

    api(Libraries.jackson_module_paranamer)
    api(Libraries.jackson_module_parameter_names)

    api(Libraries.jackson_module_afterburner)

}