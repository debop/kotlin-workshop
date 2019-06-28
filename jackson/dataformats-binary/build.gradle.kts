buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        // https://github.com/commercehub-oss/gradle-avro-plugin
        classpath("com.commercehub.gradle.plugin:gradle-avro-plugin:0.17.0")
    }
}
apply {
    plugin("com.commercehub.gradle.plugin.avro")
}

dependencies {

    api(project(":kotlin-basic"))

    api(Libraries.jackson_databind)
    api(Libraries.jackson_module_kotlin)

    api(Libraries.avro)
    api(Libraries.jackson_dataformat_avro)

    api(Libraries.jackson_dataformat_protobuf)
}

// Build script 에 아래와 같이 compile 전에 avro 를 generate 하도록 해주면 Kotlin 에서도 사용이 가능합니다.
tasks["compileKotlin"].dependsOn(tasks["generateAvroJava"])
tasks["compileTestKotlin"].dependsOn(tasks["generateTestAvroJava"])

