package org.javers.spring.boot.repository

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.boot.model.EmployeeEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

@JaversSpringDataAuditable
interface EmployeeRepositoryWithJavers: JpaRepository<EmployeeEntity, UUID>