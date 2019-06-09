package io.github.debop.jooq

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * CategoryRepository
 * @author debop (Sunghyouk Bae)
 */
@Repository
interface CategoryRepository : CrudRepository<Category, Long>, JooqRepository