plugins {
    idea
    kotlin("kapt")
}

dependencies {

    compile(project(":kotlin-basic"))
    testImplementation(project(":kotlin-tests"))

    compile(Libraries.dagger)
    kapt(Libraries.dagger_compiler)
}

idea {
    module {
        sourceDirs.plus(file("build/generated/source/kapt/main"))
        generatedSourceDirs.plus(file("build/generated/source/kapt/main"))
    }
}