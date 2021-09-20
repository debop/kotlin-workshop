package org.javers.hibernate.integration

import mu.KLogging
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldNotBeNull
import org.javers.core.Javers
import org.javers.hibernate.entity.Author
import org.javers.hibernate.entity.AuthorCrudRepository
import org.javers.hibernate.entity.Ebook
import org.javers.hibernate.entity.EbookRepository
import org.javers.repository.jql.QueryBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional
import kotlin.streams.toList

// @ContextConfiguration 을 사용하려면, @ExtendWith(SpringExtension::class) 를 같이 지정해줘야 하고,
// Test class와 메소드 모두 `open` 키워드를 지정해줘야 합니다.
//
@SpringBootTest(classes = [JaversBeanHibernateProxyConfig::class])
@Transactional
class ObjectAccessHookBeanTest {

    companion object : KLogging()

    @Autowired
    lateinit var javers: Javers

    @Autowired
    lateinit var ebookRepository: EbookRepository

    @Autowired
    lateinit var authorRepository: AuthorCrudRepository

    @PersistenceContext
    lateinit var em: EntityManager

    @Test
    fun `context loading`() {
        javers.shouldNotBeNull()
        ebookRepository.shouldNotBeNull()
        authorRepository.shouldNotBeNull()
    }

    @Test
    fun `하이버네이트 엔티티의 감사 정보를 Javers에 commit한다`() {
        // GIVEN
        val author = Author(id = "1", name = "Sunghyouk Bae")
        authorRepository.save(author)

        val ebook = Ebook(id = "1").also {
            it.title = "게임의 종말"
            it.author = author
            it.comments = mutableListOf("great book", "awesome")
        }
        ebookRepository.save(ebook)
        logger.debug { "Save ebook. ebook=$ebook" }

        val book = ebookRepository.getOne("1")

        book.shouldNotBeNull()
        book.author.shouldNotBeNull()
        book.comments!! shouldContainSame listOf("great book", "awesome")

        // WHEN
        book.author!!.name = "kazik"
        authorRepository.save(book.author!!)

        val snapshot = javers.getLatestSnapshot("1", Author::class.java).get()
        snapshot.getPropertyValue("name") shouldBeEqualTo "kazik"
        logger.debug { "Author latest snapshot=${javers.jsonConverter.toJson(snapshot)}" }

        val shadows = javers
            .findShadowsAndStream<Author>(QueryBuilder.byInstanceId("1", Author::class.java).build())
            .limit(1)
            .map { it.get() }
            .toList()
            .firstOrNull()

        author.shouldNotBeNull()
        logger.debug { "lastest author=${javers.jsonConverter.toJson(author)}" }

        val ebookSnapshot = javers.getLatestSnapshot("1", Ebook::class.java).get()
        logger.debug { "Ebook latest snapshot=${javers.jsonConverter.toJson(ebookSnapshot)}" }
    }
}