package io.github.debop.jackson.dataformat.datasources

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import mu.KLogging
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import javax.sql.DataSource

/**
 * ParseNamedDataSurcePropertiesTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 2
 */
class ParseNamedDataSurcePropertiesTest {

    companion object: KLogging()

    val propsMapper = JavaPropsMapper().apply {
        registerKotlinModule()

        enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
    }

    val yamlMapper = YAMLMapper().apply {
        registerKotlinModule()

        enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
    }

    val properties = """
        |coupang.datasources.default.connectionPool=dbcp2
        |coupang.datasources.default.driverClassName=mysql
        |coupang.datasources.default.url=jdbc:mysql://localhost:3306/test
        |coupang.datasources.default.username=sa
        |coupang.datasources.default.password=password
        |coupang.datasources.default.maxTotal=50
        |coupang.datasources.default.maxIdle=40
        |coupang.datasources.default.minIdle=10
        |coupang.datasources.default.maxWaitMillis=
        |coupang.datasources.default.lifo=true
        |coupang.datasources.default.connectionProperties=
        |coupang.datasources.read.connectionPool=hikari
        |coupang.datasources.read.driverClassName=mariadb
        |coupang.datasources.read.url=jdbc:mysql://localhost:3307/test
        |coupang.datasources.read.username=sa
        |coupang.datasources.read.password=password
        |coupang.datasources.read.connectionTimeout=5000
        |coupang.datasources.read.idleTimeout=
        |coupang.datasources.read.maxLifetime=60000
        |coupang.datasources.read.properties.1=cachePropStmts=true
        |coupang.datasources.read.properties.2=prepStmtCacheSize=250
        |coupang.datasources.read.properties.3=propStmtCacheSqlLimit=2048
        |
        """.trimMargin()

    val default = Dbcp2DataSourceProperty(driverClassName = "mysql",
                                          url = "jdbc:mysql://localhost:3306/test",
                                          username = "sa",
                                          password = "password",

                                          maxTotal = 50,
                                          maxIdle = 40,
                                          minIdle = 10,
                                          maxWaitMillis = null, // 60_000,
                                          lifo = true)

    val read = HikariDataSourceProperty(driverClassName = "mariadb",
                                        url = "jdbc:mysql://localhost:3307/test",
                                        username = "sa",
                                        password = "password",
                                        connectionTimeout = 5_000,
                                        idleTimeout = null, // 60_000L,
                                        maxLifetime = 60_000L,
                                        properties = listOf("cachePropStmts=true", "prepStmtCacheSize=250", "propStmtCacheSqlLimit=2048"))

    @Test
    fun `generate datasource properties to properties format and parse`() {

        val property = CoupangProperty(mapOf("default" to default, "read" to read))
        val root = RootProperty(property)

        val propertyString = propsMapper.writeValueAsString(root)

        logger.info { "properties=\n$propertyString" }
        propertyString shouldEqual properties

        val parsedRoot = propsMapper.readValue<RootProperty>(propertyString)

        parsedRoot.shouldNotBeNull()
        parsedRoot.coupang.datasources.size shouldEqualTo 2

        val parsedDefault = parsedRoot.coupang.datasources["default"] as Dbcp2DataSourceProperty
        val parsedRead = parsedRoot.coupang.datasources["read"] as HikariDataSourceProperty

        parsedDefault shouldEqual default
        parsedRead shouldEqual read
    }

    val yaml = """
    |---
    |coupang:
    |  datasources:
    |    default: !<dbcp2>
    |      driverClassName: "mysql"
    |      url: "jdbc:mysql://localhost:3306/test"
    |      username: "sa"
    |      password: "password"
    |      maxTotal: 50
    |      maxIdle: 40
    |      minIdle: 10
    |      maxWaitMillis: null
    |      lifo: true
    |      connectionProperties: ""
    |    read: !<hikari>
    |      driverClassName: "mariadb"
    |      url: "jdbc:mysql://localhost:3307/test"
    |      username: "sa"
    |      password: "password"
    |      connectionTimeout: 5000
    |      idleTimeout: null
    |      maxLifetime: 60000
    |      properties:
    |      - "cachePropStmts=true"
    |      - "prepStmtCacheSize=250"
    |      - "propStmtCacheSqlLimit=2048"
    |
    """.trimMargin()

    @Test
    fun `generate datasource properties to yaml format and parse`() {
        val property = CoupangProperty(mapOf("default" to default, "read" to read))
        val root = RootProperty(property)

        val yamlString = yamlMapper.writeValueAsString(root)

        logger.info { "properties=\n$yamlString" }
        yamlString shouldEqual yaml

        val parsedRoot = yamlMapper.readValue<RootProperty>(yamlString)

        parsedRoot.shouldNotBeNull()
        parsedRoot.coupang.datasources.size shouldEqualTo 2

        val parsedDefault = parsedRoot.coupang.datasources["default"] as Dbcp2DataSourceProperty
        val parsedRead = parsedRoot.coupang.datasources["read"] as HikariDataSourceProperty

        parsedDefault shouldEqual default
        parsedRead shouldEqual read
    }

    @Test
    fun `yaml data type can just string`() {
        val yaml = """
    |---
    |coupang:
    |  datasources:
    |    default: 
    |      connectionPool: "dbcp2"
    |      driverClassName: "mysql"
    |      url: "jdbc:mysql://localhost:3306/test"
    |      username: "sa"
    |      password: "password"
    |      maxTotal: 50
    |      maxIdle: 40
    |      minIdle: 10
    |      maxWaitMillis: null
    |      lifo: true
    |      connectionProperties: ""
    |    read:
    |      connectionPool: "hikari"
    |      driverClassName: "mariadb"
    |      url: "jdbc:mysql://localhost:3307/test"
    |      username: "sa"
    |      password: "password"
    |      connectionTimeout: 5000
    |      idleTimeout: null
    |      maxLifetime: 60000
    |      properties:
    |      - "cachePropStmts=true"
    |      - "prepStmtCacheSize=250"
    |      - "propStmtCacheSqlLimit=2048"
    |
    """.trimMargin()

        val parsedRoot = yamlMapper.readValue<RootProperty>(yaml)

        parsedRoot.shouldNotBeNull()
        parsedRoot.coupang.datasources.size shouldEqualTo 2

        val parsedDefault = parsedRoot.coupang.datasources["default"] as Dbcp2DataSourceProperty
        val parsedRead = parsedRoot.coupang.datasources["read"] as HikariDataSourceProperty

        parsedDefault shouldEqual default
        parsedRead shouldEqual read
    }

    data class RootProperty(val coupang: CoupangProperty)
    data class CoupangProperty(val datasources: Map<String, DataSourceProperty<out DataSource>> = emptyMap())

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "connectionPool")
    @JsonSubTypes(JsonSubTypes.Type(value = Dbcp2DataSourceProperty::class),
                  JsonSubTypes.Type(value = HikariDataSourceProperty::class))
    interface DataSourceProperty<DS: DataSource> {
        val driverClassName: String
        val url: String
        val username: String?
        val password: String?
    }

    @JsonTypeName(value = "dbcp2")
    data class Dbcp2DataSourceProperty(
        override val driverClassName: String,
        override val url: String,
        override val username: String?,
        override val password: String?,

        val maxTotal: Int?,
        val maxIdle: Int?,
        val minIdle: Int?,
        val maxWaitMillis: Int?,

        val lifo: Boolean?,

        // Properties에서 값이 없는 경우에는 empty string으로 지정된다. Parsing 후에는 empty string으로 지정된다.
        var connectionProperties: String = ""

    ): DataSourceProperty<DataSource>

    @JsonTypeName("hikari")
    data class HikariDataSourceProperty(

        override val driverClassName: String,
        override val url: String,
        override val username: String?,
        override val password: String?,

        val connectionTimeout: Int?,
        val idleTimeout: Long?,
        val maxLifetime: Long?,

        val properties: List<String> = emptyList()
    ): DataSourceProperty<DataSource>

    enum class ConnectionPoolType {
        DBPC2,
        HIKARI,
        MARIADB,
        TOMCAT
    }
}