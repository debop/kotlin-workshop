package io.github.debop.kotlin.workshop.guide

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import mu.KLogging
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

@ExperimentalCoroutinesApi
class FlowExamples {

    companion object: KLogging()

    @Test
    fun `example flow 04`() {
        fun foo(): Flow<Int> = flow {
            repeat(3) {
                delay(100)
                emit(it)
            }
        }

        runBlocking {

            launch {
                //  메인 스레드가 블락되었는지 새로운 coroutines를 생성해서 확인한다
                repeat(3) {
                    logger.debug { "I'm not blocked $it" }
                    delay(100)
                }
            }

            // Collect the flow
            foo().collect { value -> logger.debug { "emitted $value" } }
        }
    }

    @Test
    fun `repeat flow collect`() {
        fun foo(): Flow<Int> = flow {
            logger.debug { "Flow started" }
            repeat(3) {
                delay(100)
                emit(it)
            }
            logger.debug { "Flow finish" }
        }

        runBlocking {
            logger.debug { "Calling foo..." }
            val flow = foo()
            logger.debug { "Calling collect ..." }
            flow.collect { logger.debug("$it") }
            logger.debug { "Calling collect again ..." }
            flow.collect { logger.debug("$it") }
        }
    }

    @Test
    fun `timout for flow`() {
        fun foo() = flow<Int> {
            repeat(3) {
                delay(100)
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }

        runBlocking {
            // Timeout이 걸리면, flow.collect를 즉시 중단하고, null을 반환하도록 합니다.
            //
            val emitted = withTimeoutOrNull(250) {
                val xs = mutableListOf<Int>()
                foo().collect {
                    xs.add(it)
                    logger.debug { "$it" }
                }
                xs
            }
            logger.debug { "Done" }
            emitted.shouldBeNull()
        }
    }

    @Test
    fun `run asFlow`() {

        suspend fun foo(collected: MutableList<Int>) {
            // iterable을 flow로 변환해서 coroutines 환경 하에서 작업이 가능한다
            (1..3).asFlow().collect { collected.add(it) }
        }

        runBlocking {
            val collected = mutableListOf<Int>()

            foo(collected)

            collected shouldContainSame listOf(1, 2, 3)
        }
    }

    @Test
    fun `flow with map`() {

        suspend fun performRequest(request: Int): String {
            delay(100)
            return "response $request"
        }

        val responses = mutableListOf<String>()

        runBlocking {
            (1..3).asFlow()
                .map { request -> performRequest(request) }
                .collect { response ->
                    logger.debug { "response=$response" }
                    responses.add(response)
                }
        }
        responses.size shouldEqualTo 3
    }

    @Test
    fun `flow with transform`() {
        suspend fun performRequest(request: Int): String {
            delay(100)
            return "response $request"
        }

        val responses = mutableListOf<String>()

        runBlocking {
            (1..3).asFlow()
                .transform { request ->
                    emit("Making requesst $request")
                    emit(performRequest(request))
                }.collect { response ->

                    logger.debug { "response=$response" }
                    responses.add(response)
                }
        }
        responses.size shouldEqualTo 6
    }

    @Test
    fun `flow with cleanup step`() {

        fun numbers(): Flow<Int> = flow {
            try {
                emit(1)
                emit(2)
                fail("This line will not execute")
                emit(3)
            } finally {
                logger.debug { "Finally in numbers" }
            }
        }

        runBlocking {
            numbers()
                .take(2)
                .collect { value ->
                    logger.debug { "Receive $value" }
                }
        }
    }

    @Test
    fun `flow map reduce`() {
        runBlocking {
            val sum = (1..5).asFlow()
                .map { it * it }
                .reduce { acc, n -> acc + n }  // sum

            sum shouldEqualTo listOf(1, 4, 9, 16, 25).sum()
        }
    }
}