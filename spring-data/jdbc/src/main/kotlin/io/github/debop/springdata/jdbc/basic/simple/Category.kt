package io.github.debop.springdata.jdbc.basic.simple

import io.github.debop.springdata.jdbc.basic.aggregate.AgeGroup
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import java.time.LocalDateTime

/**
 * Category
 * @author debop (Sunghyouk Bae)
 */
data class Category @PersistenceConstructor constructor(var name: String,
                                                        var description: String?,
                                                        var ageGroup: AgeGroup) {
    @Id var id: Long? = null
    var inserted: Long = 0
    var created: LocalDateTime = LocalDateTime.now()

    fun timeStamp() {
        if (inserted == 0L) {
            inserted = System.currentTimeMillis()
        }
    }
}