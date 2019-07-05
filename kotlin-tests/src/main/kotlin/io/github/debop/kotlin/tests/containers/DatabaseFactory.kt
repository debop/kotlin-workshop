package io.github.debop.kotlin.tests.containers

import org.testcontainers.containers.MariaDBContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.PostgreSQLContainer

/**
 * DatabaseFactory
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 5
 */
object DatabaseFactory {

    val MySQL by lazy { MySQL5 }
    val MySQL5 by lazy { newMySQLServer(tag = MySQLContainer.DEFAULT_TAG) }
    val MySQL8 by lazy { newMySQLServer(tag = MySQLServer.MYSQL_8_TAG) }

    val MariaDB by lazy { newMariaDBServer() }

    val PostgreSQL by lazy { newPostgreSQLServer() }
    val PostgreSQL10 by lazy { newPostgreSQLServer(PostgreSQLServer.POSTGRESQL_10_TAG) }
    val PostgreSQL11 by lazy { newPostgreSQLServer(PostgreSQLServer.POSTGRESQL_11_TAG) }


    fun newMySQLServer(tag: String = MySQLContainer.DEFAULT_TAG,
                       useDefaultPort: Boolean = false,
                       configuration: String = "",
                       username: String = "test",
                       password: String = "test"): MySQLServer =
        MySQLServer(tag, useDefaultPort, configuration, username, password)

    fun newMariaDBServer(tag: String = MariaDBContainer.DEFAULT_TAG,
                         useDefaultPort: Boolean = false,
                         configuration: String = "",
                         username: String = "test",
                         password: String = "test"): MariaDBServer =
        MariaDBServer(tag, useDefaultPort, configuration, username, password)

    fun newPostgreSQLServer(tag: String = PostgreSQLContainer.DEFAULT_TAG,
                            useDefaultPort: Boolean = false,
                            username: String = "test",
                            password: String = "test"): PostgreSQLServer =
        PostgreSQLServer(tag, useDefaultPort, username, password)
}