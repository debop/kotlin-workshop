package org.javers.hibernate.integration

import mu.KLogging
import org.javers.core.Javers
import org.javers.core.MappingStyle
import org.javers.hibernate.HibernateConfig
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryBuilder
import org.javers.spring.jpa.JpaHibernateConnectionProvider
import org.javers.spring.jpa.TransactionalJaversBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = ["org.javers.hibernate.entity"])
@Import(HibernateConfig::class)
class JaversBeanHibernateProxyConfig {

    companion object : KLogging()

    @Bean
    fun javers(connectionProvider: JpaHibernateConnectionProvider,
               txManager: PlatformTransactionManager): Javers {

        logger.info { "Create Javers Bean..." }

        val sqlRepository = SqlRepositoryBuilder
            .sqlRepository()
            .withConnectionProvider(connectionProvider)
            .withDialect(DialectName.H2)
            .build()

        return TransactionalJaversBuilder
            .javers()
            .withTxManager(txManager)
            .registerJaversRepository(sqlRepository)
            .withObjectAccessHook(HibernateUnproxyObjectAccessHook<Any>())
            .withMappingStyle(MappingStyle.BEAN)  // for access property state, call getters
            .build()
    }
}