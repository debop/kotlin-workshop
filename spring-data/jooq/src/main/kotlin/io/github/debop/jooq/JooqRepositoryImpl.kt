package io.github.debop.jooq

import io.github.debop.jooq.tables.Category.CATEGORY
import org.jooq.DSLContext

/**
 * JooqRepositoryImpl
 * @author debop (Sunghyouk Bae)
 */
class JooqRepositoryImpl(private val dslContext: DSLContext) : JooqRepository {

    override fun getCategoriesWithAgeGroup(ageGroup: AgeGroup): List<Category> {
        return this.dslContext
            .select()
            .from(CATEGORY)
            .where(CATEGORY.AGE_GROUP.equal(ageGroup.name))
            .fetchInto(Category::class.java)
    }
}