import org.apache.avro.compiler.specific.SpecificCompiler

plugins {
    // Find latest release here: https://github.com/commercehub-oss/gradle-avro-plugin/releases
    // NOTE: settings.gradle.kts 의 plugin managements 에 다음과 같이 추가해야 합니다.
    /**
     * ```kotlin
     * pluginManagement {
     *     repositories {
     *         gradlePluginPortal()
     *         jcenter()
     *         mavenCentral()
     *         // for Avro plugin
     *         maven (url="https://dl.bintray.com/gradle/gradle-plugins")
     *     }
     * }
     * ```
     */
    // jackson-dataformat-avro 2.9.9 는 avro 1.8.2 를 사용하고 있어, avro plugin 을 0.17.0 이 아닌 0.16.0 을 사용한다
    // avro 1.9.0 을 사용하게 되면 avro plugin 0.17.0을 사용할 수 있습니다.
    id("com.commercehub.gradle.plugin.avro") version "0.17.0"
}

avro {
    fieldVisibility = SpecificCompiler.FieldVisibility.PRIVATE.name
    isCreateSetters = true
    // support dateTimeLogicalType since 0.17.0+ with avro 1.9.0+
    dateTimeLogicalType = "JSR310"  // "JODA"
}

// Build script 에 아래와 같이 compile 전에 avro 를 generate 하도록 해주면 Kotlin 에서도 사용이 가능합니다.
tasks["compileKotlin"].dependsOn(tasks["generateAvroJava"])
tasks["compileTestKotlin"].dependsOn(tasks["generateTestAvroJava"])

dependencies {

    api(project(":kotlin-basic"))

    api(Libraries.jackson_databind)
    api(Libraries.jackson_module_kotlin)

    api(Libraries.avro)
    api(Libraries.jackson_dataformat_avro)

    api(Libraries.jackson_dataformat_protobuf)

    // Avro 의 1.8.2의 Date는 joda-time을 사용한다. 1.9.0+ 에서는 JSR310 을 사용할 수 있다. (Jackson이 아직 1.8.x를 사용해서 어쩔 수 없다)
    testImplementation(Libraries.joda_time)
}
