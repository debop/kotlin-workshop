dependencies {

    api(Libraries.kafka_streams)
    api(Libraries.spring_kafka)
    testApi(Libraries.kafka_streams_test_utils)

    testApi(project(":kotlin-tests"))

}