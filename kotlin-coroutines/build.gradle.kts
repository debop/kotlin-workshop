dependencies {

    compile(project(":kotlin-basic"))
    testImplementation(project(":kotlin-tests"))

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${extra.get("coroutines")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${extra.get("coroutines")}")

    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("net.jodah:failsafe:${extra["failsafe"]}")
}