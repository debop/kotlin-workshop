package io.github.debop.multistore.customer

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * CustomerRepository
 * @author debop (Sunghyouk Bae)
 */
@Repository
interface CustomerRepository : JpaRepository<Customer, Long>