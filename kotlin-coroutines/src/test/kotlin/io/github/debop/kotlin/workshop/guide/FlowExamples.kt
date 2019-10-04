package io.github.debop.kotlin.workshop.guide

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import mu.KLogging
import org.amshove.kluent.shouldBeGreaterThan
import org.amshove.kluent.shouldBeLessThan
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import java.lang.System.currentTimeMillis
import kotlin.system.measureTimeMillis

@FlowPreview
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

    @Test
    fun `zip two flow`() = runBlocking<Unit> {
        val nums = (1..3).asFlow()
        val strs = flowOf("one", "two", "three")

        nums
            .zip(strs) { a, b ->

                logger.debug { "zip a=$a, b=$b" }
                "$a -> $b"
            }
            .toList()
            .size shouldEqualTo 3
    }

    @Test
    fun `zip two flow with delay`() = runBlocking<Unit> {

        val nums = (1..3).asFlow().onEach { delay(300) }
        val strs = flowOf("one", "two", "three").onEach { delay(400) }

        val startTime = currentTimeMillis()

        // nums 보다 strs 가 늦게 emit 된다. 두 flow의 emit된 순서대로 zip 합니다.
        /*
            1 -> one at 405 ms from start
            2 -> two at 807 ms from start
            3 -> three at 1212 ms from start
         */
        nums.zip(strs) { a, b -> "$a -> $b" }
            .collect {
                logger.debug { "$it at ${currentTimeMillis() - startTime} ms from start" }
            }
    }

    @Test
    fun `combine two flow with delay`() = runBlocking<Unit> {

        val nums = (1..3).asFlow().onEach { delay(300) }
        val strs = flowOf("one", "two", "three").onEach { delay(400) }

        val startTime = currentTimeMillis()

        // combine은 zip과 달리 combine 시점에 emit된 값을 사용한다.
        // 1-one, 2-one, 2-two, 3-two, 3-three 로 combine 된다.
        /*
            1 -> one at 414 ms from start
            2 -> one at 620 ms from start       // nums 2 가 emit 된 시각 (600)
            2 -> two at 818 ms from start       // strs "two" 가 emit 된 사각 (800)
            3 -> two at 922 ms from start       // nums 3 이 emit 된 시각 (900)
            3 -> three at 1220 ms from start    // strs "three" 가 emit 된 사각 (1200)
         */
        nums.combine(strs) { a, b -> "$a -> $b" }
            .collect {
                logger.debug { "$it at ${currentTimeMillis() - startTime} ms from start" }
            }
    }

    @Test
    fun `flatMapConcat example`() {

        fun requestFlow(n: Int): Flow<String> = flow {
            emit("$n: First")
            delay(500)
            emit("$n: Second")
        }

        // 복수 개의 emits을 하나의 flow로 변환합니다.
        /*
            First at 110 ms from start
            Second at 615 ms from start
            First at 719 ms from start
            Second at 1221 ms from start
            First at 1323 ms from start
            Second at 1828 ms from start
         */
        runBlocking {
            val startTime = currentTimeMillis()
            (1..3).asFlow().onEach { delay(100) }
                .flatMapConcat { requestFlow(it) }  // 복수 개의 emits 을 하나의 flow로 변환합니다.
                .collect {
                    logger.debug { "$it at ${currentTimeMillis() - startTime} ms from start" }
                }
        }
    }

    @Test
    fun `flatMapMerge example`() {

        fun requestFlow(n: Int): Flow<String> = flow {
            emit("$n: First")
            delay(500)
            emit("$n: Second")
        }

        // 여러 개의 emits을 시점별로 수행합니다.
        /*
            First at 117 ms from start
            First at 218 ms from start
            First at 325 ms from start
            Second at 619 ms from start
            Second at 723 ms from start
            Second at 830 ms from start
         */
        runBlocking {
            val startTime = currentTimeMillis()
            (1..3).asFlow().onEach { delay(100) }
                .flatMapMerge { requestFlow(it) }  // 복수 개의 emits 을 하나의 flow로 변환합니다.
                .collect {
                    logger.debug { "$it at ${currentTimeMillis() - startTime} ms from start" }
                }
        }
    }

    @Test
    fun `flatMapLatest example`() {
        fun requestFlow(n: Int): Flow<String> = flow {
            emit("$n: First")
            delay(500)
            emit("$n: Second")
        }

        // 여러 개의 emits을 시점별로 수행합니다.
        /*
            First at 109 ms from start
            First at 215 ms from start
            First at 320 ms from start
            Second at 820 ms from start
         */
        runBlocking {
            val startTime = currentTimeMillis()
            (1..3).asFlow().onEach { delay(100) }
                .flatMapLatest { requestFlow(it) }  // 복수 개의 emits 을 하나의 flow로 변환합니다.
                .collect {
                    logger.debug { "$it at ${currentTimeMillis() - startTime} ms from start" }
                }
        }
    }

    @Test
    fun `collect 실행 시 예외 catch`() {
        fun foo(): Flow<Int> = flow {
            (1..3).forEach {
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }

        runBlocking {
            var raised = false
            try {
                foo().collect {
                    logger.debug { "Emitted $it" }
                    // 일부러 예외를 발생시킨다.
                    check(it <= 1) { "Collected $it" }
                }
            } catch (e: Throwable) {
                raised = true
                e.message!! shouldContain "Collected 2"
            }
            raised.shouldBeTrue()
        }
    }

    @Test
    fun `flow chain에서 발생하는 예외 catch`() {
        fun foo(): Flow<String> =
            flow {
                (1..3).forEach {
                    logger.debug { "Emitting $it" }
                    emit(it)
                }
            }.map { value ->
                check(value <= 1) { "Crashed on $value" }
                "string $value"
            }

        runBlocking {
            var raised = false
            try {
                foo().collect { logger.debug { it } }
            } catch (e: Throwable) {
                raised = true
                logger.debug { "Caught ${e.message}" }
            }
            raised.shouldBeTrue()
        }
    }

    @Test
    fun `예외 발생 시 catch 한 후 downstream으로 emit 하기`() {
        fun foo(): Flow<String> =
            flow {
                (1..3).forEach {
                    logger.debug { "Emitting $it" }
                    emit(it)
                }
            }.map { value ->
                check(value <= 1) { "Crashed on $value" }
                "string $value"
            }

        runBlocking {
            foo()
                .catch { e -> emit("Catch $e") }  // 예외를 catch 합니다.
                .collect { value -> logger.debug { value } }
        }
    }

    @Test
    fun `downstream에서 발생하는 예외는 catch하지 못함`() {
        fun foo(): Flow<Int> = flow {
            (1..3).forEach {
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }

        assertThrows<IllegalStateException> {
            runBlocking {
                foo()
                    .catch { e -> logger.debug { "Catch $e" } } // downstream 에서 발생하는 예외는 catch 못한다
                    .collect {
                        check(it <= 1) { "Collected $it" }
                        logger.debug { it }
                    }
            }
        }
    }

    @Test
    fun `emit 발생 시마다 필요한 작업은 onEach를 사용`() {
        fun foo(): Flow<Int> = flow {
            (1..3).forEach {
                logger.debug { "Emitting $it" }
                emit(it)
            }
        }

        runBlocking {
            foo()
                .onEach { value ->
                    check(value <= 1) { "Emitted $value" }
                    fail("여기는 실행되면 안됩니다.")
                }
                .catch { e -> logger.debug { "Catch $e" } } // downstream 에서 발생하는 예외는 catch 못한다
                .collect { value ->
                    logger.debug { "Collected $value" }
                }
        }
    }

    @Test
    fun `flow 작업 후의 finalizer 추가하기`() = runBlocking<Unit> {
        var cleanup = false
        try {
            (1..3).asFlow().collect { value -> logger.debug { "Emitted: $value" } }
        } finally {
            cleanup = true
        }
        cleanup.shouldBeTrue()
    }

    @Test
    fun `onCompletion을 이용하여 cleanup 하기`() = runBlocking<Unit> {
        var cleanup = false

        (1..3).asFlow()
            .onCompletion { cleanup = true }
            .collect { value -> logger.debug { "Emitted: $value" } }

        cleanup.shouldBeTrue()
    }

    @Test
    fun `onCompletion 에서 예외 발생 시 catch하기`() {
        fun foo(): Flow<Int> = flow {
            emit(1)
            throw RuntimeException()
        }

        runBlocking {
            foo()
                .onCompletion { cause ->
                    if (cause != null) {
                        logger.debug { "Flow completed exceptionally" }
                    }
                }
                .catch { cause ->
                    logger.debug { "Caught exception" }
                }
                .collect { value ->
                    logger.debug { value }
                }
        }
    }

    @Test
    fun `onCompletion에 예외 전달이 안된다`() {


        fun foo() = (1..3).asFlow()

        assertThrows<IllegalStateException> {
            runBlocking {
                foo()
                    // collect 예외 정보를 onCompletion에서는 받을 수 없다 ??? 있어야 하는 것 아닌가?
                    // 대신 onEach {} 에서 사전 검사를 해야 할 듯
                    .onCompletion { cause -> println("Flow completed with $cause") }
                    .collect { value ->
                        check(value <= 1) { "Collected $value" }
                        println(value)
                    }
            }
        }
    }

    @Test
    fun `collect는 flow가 종료되기를 기다린다`() {

        fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

        runBlocking {
            events()
                .onEach { event -> logger.debug { "Event: $event" } }
                .collect()  // 같은 coroutine 에서 수행되므로, flow가 종료되기를 기다립니다.

            logger.debug { "Done" }
        }
    }

    @Test
    fun `flow를 다른 coroutines에서 실행`() {

        fun events(): Flow<Int> = (1..3).asFlow().onEach { delay(100) }

        runBlocking {
            events()
                .onEach { event -> logger.debug { "Event: $event" } }
                .launchIn(this)  // Launching the flow in a separate coroutine

            logger.debug { "Done" }
        }
    }
}