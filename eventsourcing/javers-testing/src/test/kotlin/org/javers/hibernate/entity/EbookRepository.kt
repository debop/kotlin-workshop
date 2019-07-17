package org.javers.hibernate.entity

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@JaversSpringDataAuditable
interface EbookRepository : JpaRepository<Ebook, String>