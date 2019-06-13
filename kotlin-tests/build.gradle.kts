dependencies {

    compile("org.junit.jupiter:junit-jupiter")
    compile("org.assertj:assertj-core")
    compile("org.testcontainers:testcontainers")
    compile("io.mockk:mockk")

    compile("io.github.benas:random-beans:3.9.0")
    compile("com.esotericsoftware:reflectasm:1.11.9")

    testImplementation("org.mongodb:mongo-java-driver:3.10.2")
    testImplementation("io.lettuce:lettuce-core:5.1.6.RELEASE")
}