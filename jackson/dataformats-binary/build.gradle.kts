
dependencies {

    api(project(":kotlin-basic"))

    api(Libraries.jackson_databind)
    api(Libraries.jackson_module_kotlin)

    api(Libraries.jackson_dataformat_avro)
    api(Libraries.jackson_dataformat_protobuf)
}