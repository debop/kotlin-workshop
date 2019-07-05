dependencies {

    api(project(":kotlin-coroutines"))
    testImplementation(project(":kotlin-tests"))

    api(Libraries.jasync_common)
    api(Libraries.jasync_mysql)

    testImplementation(Libraries.testcontainers_mariadb)
    testImplementation(Libraries.testcontainers_mysql)
    testImplementation(Libraries.testcontainers_postgresql)

    // Database Server 접속 테스트를 위해 사용합니다.
    testImplementation(Libraries.mysql_connector_java)
    testImplementation(Libraries.mariadb_java_client)
    testImplementation(Libraries.postgresql_driver)

    // NOTE: linux-x86_64 를 따로 추가해줘야 제대로 classifier가 지정된다. 이유는 모르겠지만, 이렇게 해야 제대로 된 jar를 참조한다
    testImplementation(Libraries.netty_transport_native_epoll + ":linux-x86_64")
    testImplementation(Libraries.netty_transport_native_kqueue + ":osx-x86_64")
}