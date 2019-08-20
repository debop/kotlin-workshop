dependencies {

    api(Libraries.junit_jupiter)
    api(Libraries.assertj_core)
    api(Libraries.mockk)

    api(Libraries.testcontainers)
    api(Libraries.testcontainers_junit_jupiter)
    api(Libraries.testcontainers_mariadb)
    api(Libraries.testcontainers_mysql)
    api(Libraries.testcontainers_postgresql)
    implementation(Libraries.testcontainers_kafka)
    implementation(Libraries.testcontainers_elasticsearch)

    api(Libraries.hikaricp)
    api(Libraries.mysql_connector_java)
    api(Libraries.mariadb_java_client)
    api(Libraries.postgresql_driver)

    api(Libraries.random_beans)
    api(Libraries.reflectasm)

    testImplementation(Libraries.mongo_java_driver)
    testImplementation(Libraries.lettuceCore)

    testImplementation(Libraries.spring_kafka)

    // NOTE: linux-x86_64 를 따로 추가해줘야 제대로 classifier가 지정된다. 이유는 모르겠지만, 이렇게 해야 제대로 된 jar를 참조한다
    testImplementation(Libraries.netty_transport_native_epoll + ":linux-x86_64")
    testImplementation(Libraries.netty_transport_native_kqueue + ":osx-x86_64")
}