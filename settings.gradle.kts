/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/5.4.1/userguide/multi_project_builds.html
 */
pluginManagement {
    repositories {
        gradlePluginPortal()
        jcenter()
    }
}

rootProject.name = "kotlin-workshop"

include("kotlin-basic")
include("kotlin-coroutines")
include("kotlin-dagger-example")

include("spring-boot:basic")
include("spring-boot:cache")
include("spring-boot:jcache")
include("spring-boot:webflux")
include("spring-boot:webmvc")
include("spring-boot:route")
include("spring-boot:resilience4j")

include("spring-boot:redisson")

include("spring-data:jdbc")
include("spring-data:jooq")
include("spring-data:jpa")
include("spring-data:multi-store")

include("kotlin-tests")