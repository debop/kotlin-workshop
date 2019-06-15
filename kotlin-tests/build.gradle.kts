dependencies {

    compile(Libraries.junit_jupiter)
    compile(Libraries.assertj_core)
    compile(Libraries.mockk)
    compile(Libraries.testcontainers)

    compile(Libraries.random_beans)
    compile(Libraries.reflectasm)

    testImplementation(Libraries.mongo_java_driver)
    testImplementation(Libraries.lettuceCore)
}