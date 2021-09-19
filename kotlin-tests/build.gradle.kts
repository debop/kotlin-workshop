configurations {
    testImplementation.get().extendsFrom(compileOnly.get())
}

dependencies {

    api(Libraries.junit_jupiter)
    api(Libraries.assertj_core)
    api(Libraries.mockk)

    api(Libraries.testcontainers)
    api(Libraries.testcontainers_junit_jupiter)
    compileOnly(Libraries.testcontainers_mariadb)
    compileOnly(Libraries.testcontainers_mysql)
    compileOnly(Libraries.testcontainers_postgresql)
    compileOnly(Libraries.testcontainers_kafka)
    compileOnly(Libraries.testcontainers_elasticsearch)

    compileOnly(Libraries.hikaricp)
    compileOnly(Libraries.mysql_connector_java)
    compileOnly(Libraries.mariadb_java_client)
    compileOnly(Libraries.postgresql_driver)

    api(Libraries.random_beans)
    api(Libraries.reflectasm)

    compileOnly(Libraries.mongo_driver_sync)
    compileOnly(Libraries.lettuceCore)

    compileOnly(Libraries.spring_kafka)

    // NOTE: linux-x86_64 를 따로 추가해줘야 제대로 classifier가 지정된다. 이유는 모르겠지만, 이렇게 해야 제대로 된 jar를 참조한다
    testImplementation(Libraries.netty_transport_native_epoll + ":linux-x86_64")
    testImplementation(Libraries.netty_transport_native_kqueue + ":osx-x86_64")
}