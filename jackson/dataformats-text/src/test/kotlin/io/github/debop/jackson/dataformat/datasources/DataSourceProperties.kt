package io.github.debop.jackson.dataformat.datasources

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.debop.kotlin.workshop.annotation.KotlinNoArgs

@KotlinNoArgs
data class RootProperty(val coupang: CoupangProperty)

@KotlinNoArgs
data class CoupangProperty(val datasources: List<DataSourceProperty> = emptyList())

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "connectionPool")
@JsonSubTypes(JsonSubTypes.Type(value = Dbcp2DataSourceProperty::class),
              JsonSubTypes.Type(value = HikariDataSourceProperty::class))
interface DataSourceProperty {
    val driverClassName: String
    val url: String
    val username: String?
    val password: String?
}

@KotlinNoArgs
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

    val connectionProperties: String?

): DataSourceProperty

@KotlinNoArgs
@JsonTypeName("hikaricp")
data class HikariDataSourceProperty(

    override val driverClassName: String,
    override val url: String,
    override val username: String?,
    override val password: String?,

    val connectionTimeout: Int?,
    val idleTimeout: Long?,
    val maxLifetime: Long?,

    val properties: Set<String> = emptySet()
): DataSourceProperty

enum class ConnectionPoolType {
    DBPC2,
    HIKARICP,
    MARIADB,
    TOMCAT
}