package org.javers.spring.boot.repository

import org.javers.spring.boot.model.EmployeeEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EmployeeRepository: JpaRepository<EmployeeEntity, UUID>