package org.javers.spring.boot.repository

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.boot.model.ShallowEntity
import org.springframework.data.jpa.repository.JpaRepository

@JaversSpringDataAuditable
interface ShallowEntityRepository: JpaRepository<ShallowEntity, Int>