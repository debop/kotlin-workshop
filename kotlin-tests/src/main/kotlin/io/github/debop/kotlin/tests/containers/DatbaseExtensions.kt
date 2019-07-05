package io.github.debop.kotlin.tests.containers

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import mu.KLogger
import org.testcontainers.containers.JdbcDatabaseContainer


fun <T: JdbcDatabaseContainer<T>> JdbcDatabaseContainer<T>.newDataSource(): HikariDataSource {

    val config = HikariConfig().also {
        it.driverClassName = driverClassName
        it.jdbcUrl = jdbcUrl
        it.username = username
        it.password = password
    }

    return HikariDataSource(config)
}

fun <T: JdbcDatabaseContainer<T>> JdbcDatabaseContainer<T>.setSystemProperties(logger: KLogger,
                                                                               name: String,
                                                                               title: String,
                                                                               host: String,
                                                                               port: Int) {
    // Spring 등에서 동적으로 환경설정을 수행해야 할 경위 이 값을 지정하시면 됩니다.
    // 예 : spring.datasource.url = ${testcontainer.mysql.jdbc-url}

    logger.info { "Setup System properties..." }

    System.setProperty("$CONTAINER_PREFIX.$name.host", host)
    System.setProperty("$CONTAINER_PREFIX.$name.port", port.toString())

    System.setProperty("$CONTAINER_PREFIX.$name.driver-class-name", driverClassName)
    System.setProperty("$CONTAINER_PREFIX.$name.jdbc-url", jdbcUrl)
    System.setProperty("$CONTAINER_PREFIX.$name.database", databaseName)
    System.setProperty("$CONTAINER_PREFIX.$name.username", username)
    System.setProperty("$CONTAINER_PREFIX.$name.password", password)

    logger.info {
        """
            |
            |$title :
            |    $CONTAINER_PREFIX.$name.host = $host
            |    $CONTAINER_PREFIX.$name.port = $port
            |    $CONTAINER_PREFIX.$name.driver-class-name = $driverClassName
            |    $CONTAINER_PREFIX.$name.jdbc-url = $jdbcUrl
            |    $CONTAINER_PREFIX.$name.database = $databaseName
            |    $CONTAINER_PREFIX.$name.username = $username
            |    $CONTAINER_PREFIX.$name.password = $password
            |    
            """.trimMargin()
    }

}