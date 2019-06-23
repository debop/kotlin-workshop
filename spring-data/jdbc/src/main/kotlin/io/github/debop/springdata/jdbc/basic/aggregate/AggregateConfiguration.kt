package io.github.debop.springdata.jdbc.basic.aggregate

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.data.jdbc.repository.config.JdbcConfiguration
import org.springframework.data.relational.core.mapping.event.BeforeSaveEvent
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.sql.Clob
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger

/**
 * AggregateConfiguration
 * @author debop (Sunghyouk Bae)
 */
@Configuration
@EnableJdbcRepositories
@EnableTransactionManagement
class AggregateConfiguration: JdbcConfiguration() {

    private val log: Logger = LoggerFactory.getLogger(AggregateConfiguration::class.java)

    private val idGenerator = AtomicInteger(0)

    @Bean
    fun idSetting(): ApplicationListener<BeforeSaveEvent> {
        return ApplicationListener { event ->
            val entity = event.entity
            if(entity is LegoSet) {
                setIds(entity)
            }
        }
    }

    private fun setIds(legoSet: LegoSet) {
        if(legoSet.id == 0) {
            legoSet.id = idGenerator.incrementAndGet()
            log.info("Set lego set id... id=${legoSet.id}")
        }
        legoSet.manual?.id = legoSet.id.toLong()
    }

    override fun jdbcCustomConversions(): JdbcCustomConversions {
        val clobStringConverter = Converter<Clob, String> { clob ->
            try {
                when {
                    Math.toIntExact(clob.length()) == 0 -> ""
                    else                                -> clob.getSubString(1, Math.toIntExact(clob.length()))
                }
            } catch(e: SQLException) {
                throw IllegalStateException("Fail to convert CLOB to String", e)
            }
        }
        return JdbcCustomConversions(listOf(clobStringConverter))
    }
}