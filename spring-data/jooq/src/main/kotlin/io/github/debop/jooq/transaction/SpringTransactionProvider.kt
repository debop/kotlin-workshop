package io.github.debop.jooq.transaction

import mu.KLogging
import org.jooq.TransactionContext
import org.jooq.TransactionProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.DefaultTransactionDefinition

/**
 * Spring용 jOOQ [TransactionProvider] 의 구현체
 *
 * @author debop (Sunghyouk Bae)
 */
class SpringTransactionProvider @Autowired constructor(val transactionManager: PlatformTransactionManager) : TransactionProvider {

    companion object : KLogging()

    override fun begin(ctx: TransactionContext) {
        logger.info { "Begin transaction" }

        val status = transactionManager.getTransaction(DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_NESTED))
        ctx.transaction(SpringTransaction(status))
    }

    override fun rollback(ctx: TransactionContext) {
        logger.info { "Commit transaction" }

        val status = (ctx.transaction() as SpringTransaction).status
        transactionManager.commit(status)
    }

    override fun commit(ctx: TransactionContext) {
        logger.info { "Rollback transaction" }

        val status = (ctx.transaction() as SpringTransaction).status
        transactionManager.rollback(status)
    }
}