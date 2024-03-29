
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "kotlin-workshop"

include("kotlin-basic")
include("kotlin-coroutines")
include("kotlin-futures")

// include("kotlin-dagger-example")
// include("kotlin-koin-example")

// include("grpc:kroto-plus-example")

include("jasync:jasync-mysql-example")

include("jackson:dataformats-binary")
include("jackson:dataformats-text")
include("jackson:module-examples")

include("rsocket:rsocket-communication:api")
include("rsocket:rsocket-communication:client")
include("rsocket:rsocket-communication:server")

include("spring-boot:basic")
include("spring-boot:cache2k")
include("spring-boot:cache2k-jcache")
include("spring-boot:lettuce-cache")
include("spring-boot:webflux")
include("spring-boot:webmvc")
include("spring-boot:route")
include("spring-boot:resilience4j")

include("spring-boot:redisson")

//include("spring-data:jdbc")
// include("spring-data:jooq")
include("spring-data:jpa")
include("spring-data:multi-store")
include("spring-data:redis")
include("spring-data:redis-reactive")

//include("eventsourcing:javers-kotlin")
//include("eventsourcing:javers-example")
//include("eventsourcing:javers-mongodb-example")
//include("eventsourcing:javers-testing")

include("messaging:kafka-clients-examples")
include("messaging:kafka-streams-examples")

include("text:ahocorasick-kotlin")
include("text:flashtext-kotlin")

include("utils:kotlin-uuid")

include("kotlin-tests")
