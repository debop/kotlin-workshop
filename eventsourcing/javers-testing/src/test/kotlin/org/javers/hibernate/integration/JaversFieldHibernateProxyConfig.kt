package org.javers.hibernate.integration

import mu.KLogging
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.MappingStyle
import org.javers.hibernate.HibernateConfig
import org.javers.repository.cache2k.Cache2kRepository
import org.javers.spring.jpa.JpaHibernateConnectionProvider
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
class JaversFieldHibernateProxyConfig {

    companion object : KLogging()

    @Bean
    fun javers(connectionProvider: JpaHibernateConnectionProvider,
               txManager: PlatformTransactionManager): Javers {

        JaversBeanHibernateProxyConfig.logger.info { "Create Javers Bean..." }

        //        val sqlRepository = SqlRepositoryBuilder
        //            .sqlRepository()
        //            .withConnectionProvider(connectionProvider)
        //            .withDialect(DialectName.H2)
        //            .build()
        //
        //        return TransactionalJaversBuilder
        //            .javers()
        //            .withTxManager(txManager)
        //            .registerJaversRepository(sqlRepository)
        //            .withObjectAccessHook(HibernateUnproxyObjectAccessHook<Any>())  // Hibernate Proxy Object에 대한 Hook 인데, Spring Boot 환경에서는 사용할 필요가 없다
        //            .withMappingStyle(MappingStyle.FIELD)  // for access property state, call fields
        //            .build()

        // TODO: Spring @Transactional 이 제대로 되는지 확인해야 합니다. ( TransactionalJaversBuilder을 참고해서 새로운 Transactional Javers Builder를 만들어야 합니다)
        return JaversBuilder
            .javers()
            .registerJaversRepository(Cache2kRepository())
            .withObjectAccessHook(HibernateUnproxyObjectAccessHook<Any>())
            .withMappingStyle(MappingStyle.FIELD)
            .build()
    }
}