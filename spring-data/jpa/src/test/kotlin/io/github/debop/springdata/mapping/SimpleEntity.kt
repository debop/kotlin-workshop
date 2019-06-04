package io.github.debop.springdata.mapping

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table


interface SimpleEntityRepository : JpaRepository<SimpleEntity, Long>

interface LifecycleEntityRepository : JpaRepository<LifecycleEntity, Long>

@Entity(name = "simple_simple_entity")
@Table(indexes = [Index(columnList = "name", unique = true)])
data class SimpleEntity(
    @Id
    @Column(name = "simple_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String
) {
    // equals 에서 빼기 위해
    var description: String? = null
}

// entity audit을 하려면 `@EntityListener` 를 추가해주어야 한다.
@EntityListeners(AuditingEntityListener::class)
@Entity(name = "simple_lifecycle_entity")
data class LifecycleEntity(
    @Id
    @Column(name = "simple_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(name = "createdAt", updatable = false)
    @CreatedDate
    var createdAt: LocalDateTime? = null,

    @Column(name = "updatedAt", insertable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
)
