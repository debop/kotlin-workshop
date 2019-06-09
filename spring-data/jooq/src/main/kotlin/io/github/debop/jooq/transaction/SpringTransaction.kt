package io.github.debop.jooq.transaction

import org.jooq.Transaction
import org.springframework.transaction.TransactionStatus

/**
 * Spring 용 jOOQ [Transaction] 구현체
 *
 * @author debop (Sunghyouk Bae)
 */
class SpringTransaction(val status: TransactionStatus) : Transaction