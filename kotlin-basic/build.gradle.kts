import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

kotlin {
    KotlinPlatformType.jvm
}

dependencies {

    testCompile(project(":kotlin-tests"))

}