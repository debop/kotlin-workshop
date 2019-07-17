package org.javers.spring.repository

import org.javers.spring.model.DummyObject
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DummyNoAuditJpaRepository : JpaRepository<DummyObject, String>