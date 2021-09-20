dependencies {

    api(project(":kotlin-basic"))
    //testImplementation(project(":kotlin-tests"))

    implementation(Libraries.kotlinx_coroutines_jdk8)
    // implementation(Libraries.kotlinx_coroutines_rx2)
    implementation(Libraries.kotlinx_coroutines_reactor)

    testImplementation(Libraries.kotlinx_coroutines_debug)
    testImplementation(Libraries.kotlinx_coroutines_test)
}