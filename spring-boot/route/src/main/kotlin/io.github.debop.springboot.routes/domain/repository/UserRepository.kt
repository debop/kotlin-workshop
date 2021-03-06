package io.github.debop.springboot.routes.domain.repository

import io.github.debop.springboot.routes.domain.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

/**
 * UserRepository
 * @author debop (Sunghyouk Bae)
 */
@Repository
interface UserRepository : ReactiveCrudRepository<User, String>