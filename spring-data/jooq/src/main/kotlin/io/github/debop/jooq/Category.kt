package io.github.debop.jooq

import org.springframework.data.annotation.Id

/**
 * Category
 * @author debop (Sunghyouk Bae)
 */
data class Category(
    @Id var id: Long? = null,
    var name: String? = null,
    var description: String? = null,
    var ageGroup: AgeGroup? = null
)

