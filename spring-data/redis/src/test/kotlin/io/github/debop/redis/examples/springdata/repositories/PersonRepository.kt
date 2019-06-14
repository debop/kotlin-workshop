package io.github.debop.redis.examples.springdata.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.geo.Circle
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

/**
 * PersonRepository
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 13
 */
interface PersonRepository : CrudRepository<Person, String>, QueryByExampleExecutor<Person> {

    fun findByLastname(lastname: String): List<Person>

    fun findPersonByLastname(lastname: String, page: Pageable): Page<Person>

    fun findByFirstnameAndLastname(firstname: String, lastname: String): List<Person>

    fun findByFirstnameOrLastname(firstname: String, lastname: String): List<Person>

    fun findByAddress_City(city: String): List<Person>

    fun findByAddress_LocationWithin(circle: Circle): List<Person>
}