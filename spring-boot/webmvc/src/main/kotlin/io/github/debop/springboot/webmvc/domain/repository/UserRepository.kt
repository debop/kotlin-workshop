package io.github.debop.springboot.webmvc.domain.repository

import io.github.debop.springboot.webmvc.domain.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * UserRepository
 * @author debop (Sunghyouk Bae)
 */
@Repository
interface UserRepository : CrudRepository<User, String>