package io.github.debop.kotlin.workshop.test

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineScope
import mu.KLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Suppress("EXPERIMENTAL_API_USAGE")
class TestCoroutineScopeTest {

    companion object: KLogging()

    @Test
    fun `잘못된 exception handler가 주어지면 예외를 발생시킵니다`() {
        val handler = CoroutineExceptionHandler { coroutineContext, throwable ->
            logger.debug { "Error occurred" }
        }

        assertThrows<IllegalArgumentException> {
            TestCoroutineScope(handler)
        }
    }

    @Test
    fun `잘못된 Dispatcher가 주어지면 예외를 발생시킨다`() {

        assertThrows<IllegalArgumentException> {
            TestCoroutineScope(newSingleThreadContext("incorrect call"))
        }
    }
}