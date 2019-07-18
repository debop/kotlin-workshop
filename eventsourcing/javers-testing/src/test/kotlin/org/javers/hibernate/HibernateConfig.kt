package org.javers.hibernate

import mu.KLogging
import org.javers.core.Javers
import org.javers.repository.sql.ConnectionProvider
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.CommitPropertiesProvider
import org.javers.spring.auditable.aspect.JaversAuditableAspect
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect
import org.javers.spring.jpa.JpaHibernateConnectionProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import java.util.Properties
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * HibernateConfig
 *
 * @author debop
 * @since 19. 7. 17
 */
@Configuration
class HibernateConfig {

    companion object : KLogging() {
        const val H2_URL = "jdbc:h2:mem:test"
    }

    @Bean
    fun jpaConnectionProvider(): ConnectionProvider =
        JpaHibernateConnectionProvider()

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {

        logger.info { "Create EntityManagerFactory ..." }

        val em = LocalContainerEntityManagerFactoryBean()
        em.dataSource = dataSource()
        em.setPackagesToScan("org.javers.hibernate.entity", "org.javers.spring.model")

        val adapter = HibernateJpaVendorAdapter()
        em.setJpaVendorAdapter(adapter)
        em.setJpaProperties(additionalProperties())

        return em
    }

    @Primary
    @Bean
    fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager =
        JpaTransactionManager().apply {
            entityManagerFactory = emf
        }

    @Bean
    fun exceptionTranslation(): PersistenceExceptionTranslationPostProcessor {
        return PersistenceExceptionTranslationPostProcessor()
    }

    @Bean
    fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .generateUniqueName(true)
            .build()
    }

    @Bean
    fun javersAuditableAspect(javers: Javers): JaversAuditableAspect {
        return JaversAuditableAspect(javers, authorProvider(), commitPropertiesProvider())
    }

    @Bean
    fun javersSpringDataAuditableAspect(javers: Javers): JaversSpringDataAuditableRepositoryAspect =
        JaversSpringDataAuditableRepositoryAspect(javers, authorProvider(), commitPropertiesProvider())

    @Bean
    fun authorProvider() = AuthorProvider { "unknown" }

    @Bean
    fun commitPropertiesProvider(): CommitPropertiesProvider =
        CommitPropertiesProvider { mapOf("key" to "ok") }

    private fun additionalProperties(): Properties {
        return Properties().apply {
            setProperty("hibernate.hbm2ddl.auto", "create")
            setProperty("hibernate.connection.autocommit", "false")
            setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            setProperty("hibernate.current_session_context_class", "thread")

            // NOTE: 이 설정은 spring boot의 `Open Session in View` 와 같은 역할을 수행한다. 사용하지 말 것
            // HINT: https://vladmihalcea.com/the-hibernate-enable_lazy_load_no_trans-anti-pattern/
            //
            // setProperty("hibernate.enable_lazy_load_no_trans", "true")
        }
    }
}