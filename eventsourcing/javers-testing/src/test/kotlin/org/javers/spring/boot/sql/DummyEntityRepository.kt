package org.javers.spring.boot.sql

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.boot.model.DummyEntity
import org.springframework.data.jpa.repository.JpaRepository

@JaversSpringDataAuditable
interface DummyEntityRepository: JpaRepository<DummyEntity, Int>