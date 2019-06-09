package io.github.debop.springdata.jpa

import mu.KotlinLogging.logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest(properties = [
    // define properties in application.yml
    // "spring.jpa.properties.hibernate.show_sql=false",
    // "spring.jpa.properties.hibernate.format_sql=false",
    // "spring.jpa.properties.hibernate.use_sql_comments=false",
    // "logging.level.org.hibernate.type.descriptor.sql=false"
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

    protected fun flush() {
        em.flush()
    }

    protected fun flushAndClear() {
        flush()
        clear()
    }
}