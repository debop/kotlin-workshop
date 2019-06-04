package io.github.debop.springdata.mapping.unidirection

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.OneToMany

interface CloudRepository : JpaRepository<Cloud, Long>

interface SnowflakeRepository : JpaRepository<Snowflake, Long>

@Entity(name = "unidirection_cloud")
data class Cloud(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var kind: String,
    var length: Double = 0.0
) {

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(name = "unidirection_cloud_snowflakes",
               joinColumns = [JoinColumn(name = "cloud_id")],
               inverseJoinColumns = [JoinColumn(name = "snowflake_id")])
    val producedSnowflakes: MutableSet<Snowflake> = hashSetOf()
}

@Entity(name = "unidirection_snowflake")
data class Snowflake(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var name: String) {

    var description: String? = null
}