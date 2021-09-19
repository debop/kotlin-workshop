plugins {
    idea
    kotlin("kapt")
}

dependencies {

    implementation(project(":kotlin-basic"))
    testImplementation(project(":kotlin-tests"))

    compileOnly(Libraries.dagger)
    kapt(Libraries.dagger_compiler)
}

idea {
    module {
        sourceDirs.plus(file("build/generated/source/kapt/main"))
        generatedSourceDirs.plus(file("build/generated/source/kapt/main"))
    }
}