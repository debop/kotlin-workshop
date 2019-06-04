plugins {
    idea
    kotlin("kapt")
}

dependencies {

    compile(project(":kotlin-basic"))
    testImplementation(project(":kotlin-tests"))

    compile("com.google.dagger:dagger:2.23.1")
    kapt("com.google.dagger:dagger-compiler:2.23.1")
}

idea {
    module {
        sourceDirs.plus(file("build/generated/source/kapt/main"))
        generatedSourceDirs.plus(file("build/generated/source/kapt/main"))
    }
}