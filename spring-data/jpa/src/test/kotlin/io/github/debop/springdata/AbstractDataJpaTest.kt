package io.github.debop.springdata

import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

/**
 * AbstractDataJpaTest
 *
 * @author debop
 * @since 19. 6. 4
 */
@DataJpaTest(properties = [
    "spring.jpa.properties.hibernate.format_sql=true",
    "spring.jpa.properties.hibernate.use_sql_comments=true",
    "logging.level.org.hibernate.type.descriptor.sql=trace"
])
abstract class AbstractDataJpaTest {

    companion object {
        val log = logger {}
    }

    @Autowired
    lateinit var em: TestEntityManager

    protected fun clear() {
        em.clear()
    }
}