package io.github.debop.jackson.dataformat.datasources

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.readValue
import mu.KLogging
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test

/**
 * ParseDataSourceProperties
 *
 * @author debop
 * @since 19. 6. 28
 */
class ParseDataSourceProperties {

    companion object: KLogging()

    val yamlMapper = YAMLMapper()
    val propsMapper = JavaPropsMapper()

    @Test
    fun `parse single datasource from yaml format`() {
        val yaml = """
            |coupang:
            |    datasources:
            |        -   connectionPool: hikaricp
            |            driverClassName: org.h2.Driver
            |            url: jdbc:h2:mem:test
            |            username: sa
            |            password:
            |
            |            connectionTimeout: 30000
            |            idleTimeout: 600000
            |            maxLifetime: 1800000
            |
            |            properties:
            |                - cachePropStmts=true
            |                - prepStmtCacheSize=250
            |                - propStmtCacheSqlLimit=2048
            """.trimMargin()

        val root = yamlMapper.readValue<RootProperty>(yaml)
        root.shouldNotBeNull()
        root.coupang.shouldNotBeNull()
        root.coupang.datasources.size shouldEqualTo 1

        val hikariProperty = root.coupang.datasources.first() as? HikariDataSourceProperty
        hikariProperty.shouldNotBeNull()

        hikariProperty.connectionTimeout!! shouldEqualTo 30000
        hikariProperty.idleTimeout!! shouldEqualTo 600000
        hikariProperty.maxLifetime!! shouldEqualTo 1800000

        hikariProperty.properties.size shouldEqualTo 3
        hikariProperty.properties shouldContainAll setOf("cachePropStmts=true", "prepStmtCacheSize=250", "propStmtCacheSqlLimit=2048")
    }

    @Test
    fun `parse single datsource from properties format`() {

    }

    private fun verifySingleDataSources(root: RootProperty) {

    }

    @Test
    fun `parse multiple datasources from yaml file`() {
        val input = ClassLoader.getSystemResourceAsStream("datasource.yaml")
        input.shouldNotBeNull()

        val root = yamlMapper.readValue<RootProperty>(input)
        verifyMultipleDataSources(root)

        // Object 를 properties 나 yaml 포맷의 문자열로 생성할 수도 있습니다.
        val props = propsMapper.writeValueAsProperties(root)
        logger.debug { "Yaml to Properties=$props" }
    }

    @Test
    fun `parse multiple datasources from properties file`() {
        val input = ClassLoader.getSystemResourceAsStream("datasource.properties")
        input.shouldNotBeNull()

        val root = propsMapper.readValue<RootProperty>(input)
        verifyMultipleDataSources(root)
    }

    private fun verifyMultipleDataSources(root: RootProperty) {
        root.shouldNotBeNull()
        root.coupang.shouldNotBeNull()
        root.coupang.datasources.size shouldEqualTo 2

        val hikaricp = root.coupang.datasources[0] as HikariDataSourceProperty
        hikaricp.shouldNotBeNull()

        hikaricp.connectionTimeout!! shouldEqualTo 30000
        hikaricp.idleTimeout!! shouldEqualTo 600000
        hikaricp.maxLifetime!! shouldEqualTo 1800000

        hikaricp.properties.size shouldEqualTo 3
        hikaricp.properties shouldContainAll setOf("cachePropStmts=true", "prepStmtCacheSize=250", "propStmtCacheSqlLimit=2048")

        val dbcp2 = root.coupang.datasources[1] as Dbcp2DataSourceProperty
        dbcp2.shouldNotBeNull()

        dbcp2.maxTotal!! shouldEqualTo 8
        dbcp2.maxIdle!! shouldEqualTo 8
        dbcp2.minIdle!! shouldEqualTo 0
        dbcp2.maxWaitMillis!! shouldEqualTo 100000
        dbcp2.lifo!! shouldEqualTo true
    }
}