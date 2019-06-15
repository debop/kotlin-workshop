package io.github.debop.springdata.jdbc.basic.aggregate

import org.springframework.data.jdbc.repository.query.Modifying
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

/**
 * LegoSetRepository
 * @author debop (Sunghyouk Bae)
 */
interface LegoSetRepository: CrudRepository<LegoSet, Int> {

    @Query(
        """
        SELECT m.name model_name, m.description, l.name set_name
          FROM model m JOIN lego_set l ON m.lego_set = l.id
         WHERE :age BETWEEN l.min_age and l.max_age
        """)
    fun reportModelForAge(@Param(value = "age") age: Int): List<ModelReport>

    @Modifying
    @Query(value = "UPDATE model set name = lower(name) where name <> lower(name)")
    fun lowerCaseMapKeys(): Int
}