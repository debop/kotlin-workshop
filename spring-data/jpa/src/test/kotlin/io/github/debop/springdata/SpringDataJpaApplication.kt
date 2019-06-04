package io.github.debop.springdata

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

// JPA 에서 entity audit을 수행하려면 `@EnableJpaAuditing` 을 추가해야 한다.
@EnableJpaAuditing(modifyOnCreate = true)
@SpringBootApplication
class SpringDataJpaApplication