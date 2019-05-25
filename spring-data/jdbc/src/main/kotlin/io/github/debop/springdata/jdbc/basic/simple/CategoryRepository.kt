package io.github.debop.springdata.jdbc.basic.simple

import org.springframework.data.repository.CrudRepository

/**
 * CategoryRepository
 * @author debop (Sunghyouk Bae)
 */
interface CategoryRepository : CrudRepository<Category, Long> {
}