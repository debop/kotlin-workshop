dependencies {
    api(Libraries.kafka_clients)
    api(Libraries.spring_kafka)

    testApi(project(":kotlin-tests"))
}