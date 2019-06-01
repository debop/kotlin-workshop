dependencies {

    compile("org.testcontainers:testcontainers:${extra["testcontainers"]}")
    testImplementation("org.mongodb:mongo-java-driver:3.10.2")
}