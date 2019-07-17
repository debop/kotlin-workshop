package org.javers.spring.example

import mu.KLogging
import org.javers.core.Javers
import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryBuilder
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.CommitPropertiesProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.javers.spring.auditable.aspect.JaversAuditableAspect
import org.javers.spring.auditable.aspect.springdatajpa.JaversSpringDataJpaAuditableRepositoryAspect
import org.javers.spring.jpa.JpaHibernateConnectionProvider
import org.javers.spring.jpa.TransactionalJaversBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@ComponentScan("org.javers.spring.repository")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaRepositories("org.javers.spring.repository")
class JaversSpringJpaApplicationConfig {

    companion object : KLogging()

    // region << Javers Setup >>

    @Bean
    fun javers(txManager: PlatformTransactionManager): Javers {
        val sqlRepository = SqlRepositoryBuilder
            .sqlRepository()
            .withConnectionProvider(jpaConnectionProvider())
            .withDialect(DialectName.H2)
            .build()

        return TransactionalJaversBuilder
            .javers()
            .withTxManager(txManager)
            .withObjectAccessHook(HibernateUnproxyObjectAccessHook<Any>())
            .registerJaversRepository(sqlRepository)
            .build()
    }

    @Bean
    fun javersAuditableAspect(javers: Javers): JaversAuditableAspect {
        return JaversAuditableAspect(javers, authorProvider(), commitPropertiesProvider())
    }

    @Bean
    fun javersSpringDataAuditableAspect(javers: Javers): JaversSpringDataJpaAuditableRepositoryAspect {
        return JaversSpringDataJpaAuditableRepositoryAspect(javers, authorProvider(), commitPropertiesProvider())
    }

    @Bean
    fun authorProvider(): AuthorProvider = SpringSecurityAuthorProvider()

    @Bean
    fun commitPropertiesProvider(): CommitPropertiesProvider {
        return CommitPropertiesProvider { mapOf("key" to "ok") }
    }

    @Bean
    fun jpaConnectionProvider(): ConnectionProvider = JpaHibernateConnectionProvider()

    // endregion

    // region << JPA Configuration >>

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val em = LocalContainerEntityManagerFactoryBean()
        em.setDataSource(dataSource())
        em.setPackagesToScan("org.javers.spring.model")

        val adapter = HibernateJpaVendorAdapter()
        em.setJpaVendorAdapter(adapter)
        em.setJpaProperties(additionalProperties())

        return em
    }

    @Bean
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager().apply {
            entityManagerFactory = emf
        }
    }

    @Bean
    fun exceptionTranslation(): PersistenceExceptionTranslationPostProcessor =
        PersistenceExceptionTranslationPostProcessor()

    @Bean
    fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .generateUniqueName(true)
            .build()
    }

    private fun additionalProperties(): Properties {
        return Properties().apply {
            setProperty("hibernate.hbm2ddl.auto", "create")
            setProperty("hibernate.connection.autocommit", "false")
            setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
        }
    }

    // endregion
}