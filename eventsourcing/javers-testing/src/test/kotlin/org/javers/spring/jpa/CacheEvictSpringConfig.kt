package org.javers.spring.jpa

import org.javers.core.Javers
import org.javers.hibernate.HibernateConfig
import org.javers.hibernate.entity.PersonCrudRepository
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * CacheEvictSpringConfig
 *
 * @author debop
 * @since 19. 7. 18
 */
@Configuration
@EnableJpaRepositories("org.javers.hibernate.entity")
@EnableTransactionManagement
@EnableAspectJAutoProxy
class CacheEvictSpringConfig : HibernateConfig() {

    @Bean
    fun sqlRepository(): JaversSqlRepository = SqlRepositoryBuilder
        .sqlRepository()
        .withConnectionProvider(jpaConnectionProvider())
        .withDialect(DialectName.H2)
        .build()

    @Bean
    fun javers(sqlRepository: JaversSqlRepository, txManager: PlatformTransactionManager): Javers {
        return TransactionalJaversBuilder
            .javers()
            .withTxManager(txManager)
            .registerJaversRepository(sqlRepository)
            .build()
    }

    @Bean
    fun errorThrowingService(repository: PersonCrudRepository): ErrorThrowingService =
        ErrorThrowingService(repository)
}