package io.github.debop.jooq.config

import io.github.debop.jooq.CategoryRepository
import io.github.debop.jooq.transaction.SpringTransactionProvider
import org.jooq.SQLDialect
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.data.jdbc.repository.config.JdbcConfiguration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

/**
 * CategoryConfiguration
 * @author debop (Sunghyouk Bae)
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJdbcRepositories(basePackageClasses = [CategoryRepository::class])
@Import(JdbcConfiguration::class)
class CategoryConfiguration {

    @Autowired
    private lateinit var dataSource: DataSource

    @Bean
    fun transactionManager(): PlatformTransactionManager =
        DataSourceTransactionManager(dataSource)

    @Bean
    fun connectionProvider() =
        DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))

    @Bean
    fun transactionProvider() =
        SpringTransactionProvider(transactionManager())

    @Bean
    fun dsl() =
        DefaultDSLContext(configuration())

    fun configuration(): DefaultConfiguration {
        return DefaultConfiguration().also {
            // NOTE: DefaultConfiguration 내에도 connectionProvider() 메소드가 있다. 그래서 apply가 아닌 also 로 구분하도록 했다   
            it.set(this@CategoryConfiguration.connectionProvider())
            it.setSQLDialect(SQLDialect.H2)
            it.setTransactionProvider(transactionProvider())
        }
    }
}