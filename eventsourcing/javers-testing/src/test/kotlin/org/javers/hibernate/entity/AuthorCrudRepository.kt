package org.javers.hibernate.entity

import org.javers.spring.annotation.JaversSpringDataAuditable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// TODO: `@Repository`가 지정되어 있거나 [CrudRepository]를 상속한 타입에
// TODO: `@JaversSpringDataAuditable`을 자동으로 적용할 수 있도록 하자
@Repository
@JaversSpringDataAuditable
interface AuthorCrudRepository : JpaRepository<Author, String> {
}