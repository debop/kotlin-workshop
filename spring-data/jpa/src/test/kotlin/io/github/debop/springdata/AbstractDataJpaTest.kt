package io.github.debop.springdata

import mu.KotlinLogging.logger
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

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
}