dependencies {

    api(project(":kotlin-basic"))
    testImplementation(project(":kotlin-tests"))

    api(Libraries.koin_core_ext)
    testImplementation(Libraries.koin_test)

    testImplementation(Libraries.mockito_junit_jupiter)
    testImplementation(Libraries.mockito_kotlin)
}