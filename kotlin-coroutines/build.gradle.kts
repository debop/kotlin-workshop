dependencies {

    api(project(":kotlin-basic"))
    testImplementation(project(":kotlin-tests"))

    api(Libraries.kotlinx_coroutines_jdk8)
}