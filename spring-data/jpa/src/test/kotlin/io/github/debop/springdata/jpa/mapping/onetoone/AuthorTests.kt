package io.github.debop.springdata.jpa.mapping.onetoone

import io.github.debop.springdata.jpa.AbstractDataJpaTest
import mu.KotlinLogging.logger
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

/**
 * AuthorTests
 * @author debop (Sunghyouk Bae)
 */
class AuthorTests : AbstractDataJpaTest() {

    private val log = logger {}

    @Autowired
    private lateinit var authorRepo: AuthorRepository

    @Test
    fun `author and biography`() {
        val author = Author(name = "debop").apply {
            biography.information = "Sunghyouk Bae"
            picture.path = "file:/a/b/c"
        }
        authorRepo.saveAndFlush(author)
        author.id.shouldNotBeNull()
        author.biography.id.shouldNotBeNull()
        author.picture.id.shouldNotBeNull()
        clear()

        log.debug { "load biography" }
        val biography = em.find(Biography::class.java, author.id)
        biography.shouldNotBeNull()
        biography.author shouldEqual author

        /*
            select
                author0_.id as id1_35_0_,
                author0_.name as name2_35_0_
            from
                onetoone_author author0_
            where
                author0_.id=?
         */
        val author2 = authorRepo.findByIdOrNull(author.id)!!
        author2 shouldEqual author

        author2.biography shouldEqual biography
        author2.biography.information shouldEqual biography.information

        author2.picture shouldEqual author.picture

        // cascade delete (author -> biography, picture)
        authorRepo.delete(author2)
        flushAndClear()

        authorRepo.existsById(author.id!!).shouldBeFalse()
        em.find(Biography::class.java, author.biography.id!!).shouldBeNull()
        em.find(AuthorPicture::class.java, author.picture.id!!).shouldBeNull()
    }
}