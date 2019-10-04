package io.github.debop.kotlin.workshop.guide

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import mu.KLogging
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import kotlin.system.measureTimeMillis

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
    fun `flow를 map과 reduce 하기`() {
        runBlocking {
            val sum = (1..5).asFlow()
                .map { it * it }
                .reduce { acc, n -> acc + n }  // sum

            sum shouldEqualTo listOf(1, 4, 9, 16, 25).sum()
        }
    }

    @Test
    fun `flow를 filtering 하기`() {
        // Flow는 Sequence와 같이 요소 하나씩 chained method를 수행합니다. 단 Coroutine 기반으로 수행한다는 점이 다르다 
        runBlocking {
            (1..5).asFlow()
                .filter {
                    logger.debug { "Filter $it" }
                    it % 2 == 0
                }
                .map {
                    logger.debug { "Map $it" }
                    "string $it"
                }
                .collect {
                    logger.debug { "Collect $it" }
                }
        }
    }

    @Test
    fun `호출 Thread와 flow thread가 같다`() {

        fun foo() = flow {
            logger.debug { "Started foo flow" }
            (1..3).forEach {
                emit(it)
            }
        }

        runBlocking {
            logger.debug { "Call foo" }
            foo().collect { logger.debug { "Collected $it" } }
        }
    }

    @Test
    fun `wrong way to change context in flow builder`() {

        fun foo() = flow {

            // NOTE: flow builder 내애서 CPU-consuming 작업 시 context를 변경하고자 할 때 잘못된 방법
            // NOTE: 아래의 flowOn(Dispatchers.Default) 를 사용해야 한다
            withContext(Dispatchers.Default) {
                (1..3).forEach {
                    Thread.sleep(100)  // CPU-
                    emit(it)
                }
            }
        }

        assertThrows<IllegalStateException> {
            runBlocking {
                foo().collect { logger.debug { "Collected $it" } }
            }
        }
    }

    @Test
    fun `호출 Thread와 flow thread를 다르게 하는 옳바른 방법`() {

        fun foo() = flow {
            logger.debug { "Started foo flow" }
            (1..3).forEach {
                Thread.sleep(100)       // Blocking 방식으로 CPU 소비를 구현
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }.flowOn(Dispatchers.Default) // NOTE: CPU-bounded 작업에 대해 context 변환 시 옳바른 방법

        runBlocking {
            logger.debug { "Call foo" }
            foo().collect { logger.debug { "Collected $it" } }
        }
    }

    @Test
    fun `Non Blocking 방식으로 지체하기 - Back pressure 표현`() {

        fun foo(): Flow<Int> = flow {
            (1..3).forEach {
                delay(100)   // 비동기 방식으로 100ms 대기
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }
        // NOTE: flowOn(Dispatchers.Default)를 추가하면 flow는 독자적으로 emit 하고, 이를 main thread 에서 collect한다
        // HINT: 다음 예제에서는 back pressure를 buffer 를 명시해서 수행 할 수 있다
        // .flowOn(Dispatchers.Default)

        runBlocking {
            val time = measureTimeMillis {
                foo().collect {
                    delay(300)  // 받은 데이터 처리를 300 ms 까지 지체시킨다 - Back pressure
                    logger.debug { "Collected $it" }
                }
            }
            logger.debug { "Collected in $time ms" }

            time shouldBeGreaterThan 300 * 3 + 100 * 3
        }
    }

    @Test
    fun `Back pressure를 buffer()를 이용하여 관리`() {
        fun foo(): Flow<Int> = flow {
            (1..3).forEach {
                delay(100)   // 비동기 방식으로 100ms 대기
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }

        runBlocking {
            val time = measureTimeMillis {
                foo()
                    .buffer() // buffer emissons, don't wait
                    .collect {
                        delay(300)  // 받은 데이터 처리를 300 ms 까지 지체시킨다 - Back pressure
                        logger.debug { "Collected $it" }
                    }
            }
            logger.debug { "Collected in $time ms" }
            time shouldBeLessThan (300 * 3 + 100 * 3)
        }
    }

    @Test
    fun `Back pressure 무시하기 - conflate`() {
        fun foo(): Flow<Int> = flow {
            (1..3).forEach {
                delay(100)   // 비동기 방식으로 100ms 대기
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }

        runBlocking {
            val time = measureTimeMillis {
                foo()
                    .conflate()   // 모든 emission을 처리하는 것이 아니라, 처리 못할 것은 무시한다
                    .collect {
                        delay(300)  // 받은 데이터 처리를 300 ms 까지 지체시킨다 - Back pressure
                        logger.debug { "Collected $it" }
                    }
            }
            logger.debug { "Collected in $time ms" }
            time shouldBeLessThan (300 * 3)   // 두 개만 처리한다
        }
    }

    @Test
    fun `Back pressure 무시하기 - collectLatest`() {
        fun foo(): Flow<Int> = flow {
            (1..3).forEach {
                delay(100)   // 비동기 방식으로 100ms 대기
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }

        runBlocking {
            val time = measureTimeMillis {
                foo()
                    // cancel & restart on the latest value
                    // 새로운 emit이 들어오면 새로운 coroutines에서 collect를 수행하다가, latest만 collect하고, 나머지는 중단 시킵니다.
                    .collectLatest {
                        logger.debug { "Collecting $it" }
                        delay(300)  // 받은 데이터 처리를 300 ms 까지 지체시킨다 - Back pressure
                        logger.debug { "Collected $it" }
                    }
            }
            logger.debug { "Collected in $time ms" }
            time shouldBeLessThan (300 * 3)   // 두 개만 처리한다
        }
    }
}