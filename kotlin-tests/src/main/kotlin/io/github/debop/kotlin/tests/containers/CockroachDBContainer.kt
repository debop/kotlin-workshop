package io.github.debop.kotlin.tests.containers

import mu.KLogging
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import java.time.Duration

/**
 * CockroachDBServer
 * @author debop (Sunghyouk Bae)
 */
open class CockroachDBContainer<SELF: CockroachDBContainer<SELF>>(dockerImageName: String)
    : JdbcDatabaseContainer<SELF>(dockerImageName) {

    companion object: KLogging() {
        const val IMAGE = "cockroachdb/cockroach"
        const val NAME = "cockroach"
        const val DEFAULT_TAG = "v19.1.3"

        const val REST_API_PORT = 8080
        const val COCKROACH_PORT = 26257

        // CockroachDB는 PostgreSQL Jdbc Driver를 사용합니다
        const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
    }

    init {
        this.waitStrategy = HttpWaitStrategy()
            .forPath("/health")
            .forPort(REST_API_PORT)
            .forStatusCode(200)
            .withStartupTimeout(Duration.ofMinutes(1))
    }

    private var _username: String? = "root"
    private var _password: String? = ""
    private var _databaseName: String? = "postgres"

    override fun getUsername(): String? = _username
    override fun getPassword(): String? = _password
    override fun getDatabaseName(): String? = _databaseName

    override fun withUsername(username: String?): SELF {
        _username = username
        return self()
    }

    override fun withPassword(password: String?): SELF {
        _password = password
        return self()
    }

    override fun withDatabaseName(dbName: String?): SELF {
        _databaseName = dbName
        return self()
    }

    override fun getDriverClassName(): String = DRIVER_CLASS_NAME

    override fun getJdbcUrl(): String =
        "jdbc:postgresql://$containerIpAddress:${getMappedPort(COCKROACH_PORT)}/$_databaseName"

    override fun getTestQueryString(): String = "SELECT 1;"

    override fun configure() {
        super.configure()
        addExposedPort(COCKROACH_PORT)
        addEnv("COCKROACH_DATABASE", _databaseName)
        addEnv("COCKROACH_USER", _username)
        _password?.run {
            addEnv("COCKROACH_PASSWORD", this)
        }
        addEnv("name", "roach1")
        addEnv("hostname", "roach1")
        addEnv("net", "roachnet")

        addExposedPorts(COCKROACH_PORT, REST_API_PORT)

        withCommand("start --insecure")

        startupAttempts = 3
    }
}