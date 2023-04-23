plugins {
    id(BuildPlugins.avro) version BuildPlugins.Versions.avro
}

avro {
    isCreateSetters.set(true)
    isCreateOptionalGetters.set(false)
    isGettersReturnOptional.set(false)
    fieldVisibility.set("PUBLIC")
    outputCharacterEncoding.set("UTF-8")
    stringType.set("String")
    templateDirectory.set(null as String?)
    isEnableDecimalLogicalType.set(true)
}

// Build script 에 아래와 같이 compile 전에 avro 를 generate 하도록 해주면 Kotlin 에서도 사용이 가능합니다.
tasks["compileKotlin"].dependsOn(tasks["generateAvroJava"])
tasks["compileTestKotlin"].dependsOn(tasks["generateTestAvroJava"])

dependencies {

    api(project(":kotlin-basic"))
    testApi(project(":kotlin-tests"))

    api(Libraries.jackson_databind)
    api(Libraries.jackson_module_kotlin)

    api(Libraries.avro)
    api(Libraries.avro_kotlin)
    api(Libraries.snappy_java)
    api(Libraries.jackson_dataformat_avro)

    api(Libraries.jackson_dataformat_protobuf)

    // Avro 의 1.8.2의 Date는 joda-time을 사용한다. 1.9.0+ 에서는 JSR310 을 사용할 수 있다. (Jackson이 아직 1.8.x를 사용해서 어쩔 수 없다)
    testImplementation(Libraries.joda_time)
}
