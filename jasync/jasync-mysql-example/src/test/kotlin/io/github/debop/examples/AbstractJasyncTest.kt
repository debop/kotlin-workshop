package io.github.debop.examples

import io.github.debop.kotlin.tests.containers.DatabaseFactory
import mu.KLogging

/**
 * AbstractJasyncTest
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 5
 */
abstract class AbstractJasyncTest {

    companion object: KLogging() {
        val MYSQL by lazy { DatabaseFactory.newMySQLServer() }
    }
}