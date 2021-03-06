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
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
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
            .withObjectAccessHook(HibernateUnproxyObjectAccessHook<Any>()) // Hibernate Proxy Object에 대한 Hook 인데, Spring Boot 환경에서는 사용할 필요가 없다
            .withMappingStyle(MappingStyle.BEAN)  // for access property state, call getters (기본값은 FIELD)
            .build()

        // TODO: Spring @Transactional 이 제대로 되는지 확인해야 합니다. ( TransactionalJaversBuilder을 참고해서 새로운 Transactional Javers Builder를 만들어야 합니다)
        //        return JaversBuilder
        //            .javers()
        //            .registerJaversRepository(Cache2kRepository())
        //            .withObjectAccessHook(HibernateUnproxyObjectAccessHook<Any>())
        //            .withMappingStyle(MappingStyle.BEAN)  // for access property state, call getters (기본값은 FIELD)
        //            .build()
    }
}