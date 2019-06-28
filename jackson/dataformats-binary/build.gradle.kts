plugins {
    kotlin("plugin.noarg")
}

noArg {
    annotation("io.github.debop.kotlin.workshop.annotation.KotlinNoArgs")
}

dependencies {

    api(project(":kotlin-basic"))

    api(Libraries.jackson_databind)
    api(Libraries.jackson_module_kotlin)

    api(Libraries.jackson_dataformat_avro)
    api(Libraries.jackson_dataformat_protobuf)
}