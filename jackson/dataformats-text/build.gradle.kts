
dependencies {

    api(project(":kotlin-basic"))

    api(Libraries.jackson_databind)
    api(Libraries.jackson_module_kotlin)

    api(Libraries.jackson_dataformat_csv)
    api(Libraries.jackson_dataformat_properties)
    api(Libraries.jackson_dataformat_yaml)

}