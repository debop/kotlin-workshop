import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    kotlin("plugin.noarg")
}

noArg {
    annotation("io.github.debop.kotlin.workshop.annotation.KotlinValueClass")
}


kotlin {
    KotlinPlatformType.jvm
}

dependencies {

    api(project(":kotlin-basic"))

    api(Libraries.jackson_databind)
    api(Libraries.jackson_module_kotlin)

    api(Libraries.jackson_dataformat_csv)
    api(Libraries.jackson_dataformat_properties)
    api(Libraries.jackson_dataformat_yaml)

    api(Libraries.jackson_dataformat_avro)
    api(Libraries.jackson_dataformat_protobuf)

}