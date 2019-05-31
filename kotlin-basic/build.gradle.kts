dependencies {

    testCompile(project(":kotlin-tests"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra.get("coroutines")}")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${extra.get("coroutines")}")

    compile("io.github.microutils:kotlin-logging:1.6.26")

    compile("org.apache.commons:commons-lang3:3.9")
    compile("net.jodah:failsafe:${extra["failsafe"]}")
}