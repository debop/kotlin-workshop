package org.javers.spring.jpa

import mu.KLogging
import org.javers.hibernate.entity.Person
import org.javers.hibernate.entity.PersonCrudRepository
import org.springframework.transaction.annotation.Transactional

/**
 * ErrorThrowingService
 *
 * @author debop
 * @since 19. 7. 18
 */
open class ErrorThrowingService(private val repository: PersonCrudRepository) {

    companion object : KLogging()

    @Transactional
    open fun saveAndThrow(person: Person) {
        repository.save(person)

        logger.warn { "테스트를 위해 엔티티 저장 후 예외를 발생시킵니다!!!" }
        throw RuntimeException("rollback")
    }
}