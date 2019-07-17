package org.javers.hibernate.integration

import org.amshove.kluent.shouldNotBeNull
import org.javers.core.Javers
import org.javers.hibernate.entity.AuthorCrudRepository
import org.javers.hibernate.entity.EbookRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(classes = [JaversFieldHibernateProxyConfig::class])
@Transactional
class ObjectAccessHookFieldTest {

    @Autowired
    lateinit var javers: Javers

    @Autowired
    lateinit var ebookRepository: EbookRepository

    @Autowired
    lateinit var authorRepository: AuthorCrudRepository

    @Test
    open fun `context loading`() {
        javers.shouldNotBeNull()
        ebookRepository.shouldNotBeNull()
        authorRepository.shouldNotBeNull()
    }

    @Test
    open fun `하이버네이트 엔티티의 unproxy된 정보를 Javers에 commit한다`() {

    }
}