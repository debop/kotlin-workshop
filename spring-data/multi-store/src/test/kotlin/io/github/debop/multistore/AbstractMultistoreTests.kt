package io.github.debop.multistore

import io.github.debop.kotlin.tests.containers.MongoDBContainer
import mu.KLogging

/**
 * AbstractMultistoreTests
 * @author debop (Sunghyouk Bae)
 */
abstract class AbstractMultistoreTests {

    companion object : KLogging() {

        private val mongoDB = MongoDBContainer.instance
    }
}