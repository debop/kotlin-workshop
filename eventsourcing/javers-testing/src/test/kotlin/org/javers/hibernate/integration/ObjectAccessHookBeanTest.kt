package org.javers.hibernate.integration

import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import org.javers.core.Javers
import org.javers.hibernate.entity.Author
import org.javers.hibernate.entity.AuthorCrudRepository
import org.javers.hibernate.entity.Ebook
import org.javers.hibernate.entity.EbookRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional

@ContextConfiguration(classes = [JaversBeanHibernateProxyConfig::class])
@Transactional
class ObjectAccessHookBeanTest {

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
        // GIVEN
        val author = Author(id = "1", name = "Sunghyouk Bae")
        authorRepository.save(author)

        val ebook = Ebook(id = "1").also {
            it.title = "게임의 종말"
            it.author = author
            it.comments.addAll(listOf("great book", "awesome"))
        }
        ebookRepository.save(ebook)

        val book = ebookRepository.getOne("1")
        book.author shouldBeInstanceOf HibernateProxy::class.java
        Hibernate.isInitialized(book.author).shouldBeFalse()

        // WHEN
        book.author!!.name = "kazik"
        authorRepository.save(book.author!!)

        val snapshot = javers.getLatestSnapshot("1", Author::class.java).get()
        snapshot.getPropertyValue("name") shouldEqual "kazik"
    }
}