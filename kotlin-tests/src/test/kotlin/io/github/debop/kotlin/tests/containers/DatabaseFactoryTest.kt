package io.github.debop.kotlin.tests.containers

import mu.KLogging
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import javax.sql.DataSource

/**
 * DatabaseFactoryTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 5
 */
class DatabaseFactoryTest {

    companion object: KLogging()

    @Test
    fun `create mysql 5 server`() {
        DatabaseFactory.newMySQLServer().use { mysql5 ->
            mysql5.shouldNotBeNull()
            mysql5.isRunning.shouldBeTrue()

            mysql5.newDataSource().use { datasource ->
                datasource.verifyConnect()
            }
        }
    }

    @Disabled("MySQL 8.0+ 서버 실행에 실패합니다. Mac에서는 제대로 동작하는데, Linux에서는 동작하지 않습니다.")
    @Test
    fun `create mysql 8 server`() {
        DatabaseFactory.newMySQLServer(MySQLServer.MYSQL_8_TAG).use { mysql8 ->
            mysql8.shouldNotBeNull()
            mysql8.isRunning.shouldBeTrue()

            mysql8.newDataSource().use { datasource ->
                datasource.verifyConnect()
            }
        }
    }

    @Test
    fun `create mariadb server`() {
        DatabaseFactory.newMariaDBServer().use { mariadb ->
            mariadb.shouldNotBeNull()
            mariadb.isRunning.shouldBeTrue()

            mariadb.newDataSource().use { datasource ->
                datasource.verifyConnect()
            }
        }
    }

    @Test
    fun `create postgresql server`() {
        DatabaseFactory.newPostgreSQLServer().use { postgresql ->
            postgresql.shouldNotBeNull()
            postgresql.isRunning.shouldBeTrue()

            postgresql.newDataSource().use { datasource ->
                datasource.verifyConnect()
            }
        }
    }

    @Test
    fun `create postgresql 10 server`() {
        DatabaseFactory.newPostgreSQLServer(PostgreSQLServer.POSTGRESQL_10_TAG).use { postgresql ->
            postgresql.shouldNotBeNull()
            postgresql.isRunning.shouldBeTrue()

            postgresql.newDataSource().use { datasource ->
                datasource.verifyConnect()
            }
        }
    }

    @Test
    fun `create postgresql 11 server`() {
        DatabaseFactory.newPostgreSQLServer(PostgreSQLServer.POSTGRESQL_11_TAG).use { postgresql ->
            postgresql.shouldNotBeNull()
            postgresql.isRunning.shouldBeTrue()

            postgresql.newDataSource().use { datasource ->
                datasource.verifyConnect()
            }
        }
    }

    @Test
    fun `create cockroach db server`() {
        DatabaseFactory.newCockroachDBServer().use { cockroach ->
            cockroach.shouldNotBeNull()
            cockroach.isRunning.shouldBeTrue()

            cockroach.newDataSource().use { datasource ->
                datasource.verifyConnect()
            }
        }
    }

    @Test
    fun `create cockroach db server with default port`() {
        DatabaseFactory.newCockroachDBServer(useDefaultPort = true).use { cockroach ->
            cockroach.shouldNotBeNull()
            cockroach.isRunning.shouldBeTrue()
            cockroach.port shouldEqualTo CockroachDBContainer.COCKROACH_PORT

            cockroach.newDataSource().use { datasource ->
                datasource.verifyConnect()
            }
        }
    }

    private fun DataSource.verifyConnect() {
        connection.use { conn ->
            conn.isValid(1).shouldBeTrue()

            conn.createStatement().use { stmt ->
                logger.trace { "Before execute sql statement..." }
                stmt.execute("SELECT 1").shouldBeTrue()
                logger.trace { "After execute sql statement" }
            }
        }
    }
}