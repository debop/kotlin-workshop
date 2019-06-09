package io.github.debop.springdata.jpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.repository.config.BootstrapMode

// JPA 에서 entity audit을 수행하려면 `@EnableJpaAuditing` 을 추가해야 한다.
@EnableJpaAuditing(modifyOnCreate = true)
// BootstrapMode.LAZY 를 사용하면 Loading 속도가 빠르다 
@EnableJpaRepositories(bootstrapMode = BootstrapMode.LAZY)
@SpringBootApplication
class SpringDataJpaApplication