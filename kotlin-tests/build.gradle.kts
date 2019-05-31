dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))

    compile("org.testcontainers:testcontainers:${extra["testcontainers"]}")

    compile("org.junit.jupiter:junit-jupiter:${extra["junit.jupiter"]}")
    compile("org.amshove.kluent:kluent:1.45")

    compile("io.github.microutils:kotlin-logging:1.6.26")
    compile("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.mongodb:mongo-java-driver:3.10.2")
}