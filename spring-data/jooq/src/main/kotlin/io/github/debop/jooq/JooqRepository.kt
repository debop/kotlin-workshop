package io.github.debop.jooq

/**
 * JooqRepository
 * @author debop (Sunghyouk Bae)
 */
interface JooqRepository {

    fun getCategoriesWithAgeGroup(ageGroup: AgeGroup): List<Category>
}