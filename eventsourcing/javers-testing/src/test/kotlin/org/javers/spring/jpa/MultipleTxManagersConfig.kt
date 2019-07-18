package org.javers.spring.jpa

import org.javers.core.Javers
import org.javers.hibernate.HibernateConfig
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.TransactionManagementConfigurer
import javax.persistence.EntityManagerFactory

@Configuration
@EnableJpaRepositories("org.javers.hibernate.entity")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@Import(HibernateConfig::class)
class MultipleTxManagersConfig : HibernateConfig(), TransactionManagementConfigurer {

    @Autowired
    private lateinit var emf: EntityManagerFactory

    override fun annotationDrivenTransactionManager(): PlatformTransactionManager {
        return transactionManager(emf)
    }

    @Bean
    fun secondTransactionManager(emf: EntityManagerFactory): PlatformTransactionManager =
        JpaTransactionManager().apply {
            entityManagerFactory = emf
        }

    @Bean
    fun sqlRepository(): JaversSqlRepository =
        SqlRepositoryBuilder
            .sqlRepository()
            .withConnectionProvider(jpaConnectionProvider())
            .withDialect(DialectName.H2)
            .build()

    @Bean
    fun javers(sqlRepository: JaversSqlRepository,
               @Qualifier("transactionManager") txManager: PlatformTransactionManager): Javers =
        TransactionalJaversBuilder
            .javers()
            .withTxManager(txManager)
            .registerJaversRepository(sqlRepository)
            .build()
}