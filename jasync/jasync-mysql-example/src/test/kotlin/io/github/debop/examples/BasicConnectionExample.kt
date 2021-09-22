package io.github.debop.examples

import com.github.jasync.sql.db.general.ArrayRowData
import com.github.jasync.sql.db.mysql.MySQLConnection
import com.github.jasync.sql.db.mysql.MySQLConnectionBuilder
import com.github.jasync.sql.db.pool.ConnectionPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import mu.KLogging
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.RepeatedTest
import java.time.Duration

/**
 * BasicConnectionExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 5
 */
class BasicConnectionExample: AbstractJasyncTest() {

    companion object: KLogging()

    private lateinit var connection: ConnectionPool<MySQLConnection>

    @BeforeAll
    fun setup() {
        connection = MySQLConnectionBuilder.createConnectionPool {
            host = MYSQL.host
            port = MYSQL.port
            username = MYSQL.username
            password = MYSQL.password
            database = MYSQL.databaseName
            maxActiveConnections = 100
            maxIdleTime = Duration.ofMinutes(15).toMillis()
            maxPendingQueries = 1000
            connectionValidationInterval = Duration.ofSeconds(60).toMillis()
        }
    }

    @AfterAll
    fun cleanup() {
        connection.disconnect().get()
    }

    @RepeatedTest(10)
    fun `connection to mysql by completableFuture`() {
        connection.connect().thenAccept { conn ->
            val future1 = conn.sendPreparedStatement("select 1")
                .whenComplete { result, _ ->
                    logger.debug { (result.rows[0] as ArrayRowData).columns.toList() }
                }
            val future2 = conn.sendPreparedStatement("SELECT 2")
                .whenComplete { result, _ ->
                    logger.debug { (result.rows[0] as ArrayRowData).columns.toList() }
                }

            future1.join()
            future2.join()
        }
            .join()
    }

    @RepeatedTest(10)
    fun `connection to mysql by coroutines`() = runBlocking<Unit> {
        val conn = connection.connect().await()

        withContext(Dispatchers.IO) {
            val result1 = conn.sendPreparedStatement("SELECT 1")
            val result2 = conn.sendPreparedStatement("SELECT 2")

            val rowData1 = result1.await().rows[0] as ArrayRowData
            val rowData2 = result2.await().rows[0] as ArrayRowData
            logger.debug { rowData1.columns.toList() }
            logger.debug { rowData2.columns.toList() }
        }
    }
}