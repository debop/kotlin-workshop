package io.github.debop.futures.jdk8

import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import java.io.IOException
import java.util.concurrent.CompletableFuture

class CompletableFutureTest {

    val success = 1.asCompletableFuture()
    val failed = IllegalArgumentException().asCompletableFuture<Int>()

    @Nested
    inner class MapTest {

        @Test
        fun `성공한 Future는 map을 수행합니다`() {
            success.map { it + 1 }.get() shouldBeEqualTo 2
        }

        @Test
        fun `실패한 Future는 map을 수행할 때 예외를 발생시킨다`() {
            val error = assertThrows<Exception> {
                failed.map { it + 1 }.get()
            }
            error.cause shouldBeInstanceOf IllegalArgumentException::class
        }
    }

    @Nested
    inner class FlatMapTest {

        @Test
        fun `성공한 Future는 flatMap을 수행합니다`() {
            success.flatMap { it.asCompletableFuture() }.get() shouldBeEqualTo success.get()
            success.flatMap { immediateFuture { it + 1 } }.get() shouldBeEqualTo 2
        }

        @Test
        fun `실패한 Future는 flatMap을 수행할 때 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.flatMap { immediateFuture { it + 1 } }.get()
            }
                .cause shouldBeInstanceOf IllegalArgumentException::class
        }
    }

    @Nested
    inner class FlattenTest {
        @Test
        fun `성공한 Future는 flatten을 수행합니다`() {
            futureOf { futureOf { 1 } }.flatten().get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 flatten 수행 시 예외를 발생시킵니다`() {
            assertThrows<Exception> {
                futureOf { futureOf { throw RuntimeException() } }.flatten().get()
            }
                .cause shouldBeInstanceOf RuntimeException::class
        }
    }

    @Nested
    inner class FilterTest {
        @Test
        fun `성공한 Future 이고 filter를 만족하면 결과를 반환한다`() {
            success.filter { it == 1 }.get() shouldBeEqualTo 1
        }

        @Test
        fun `성공한 Future 지만 filter 를 만족하지 못하면 NoSuchElementException 예외를 발생시킨다`() {
            assertThrows<Exception> {
                success.filter { it == 2 }.get()
            }
                .cause shouldBeInstanceOf NoSuchElementException::class

        }

        @Test
        fun `실패한 Future는 filter 시 자신의 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.filter { it == 1 }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }
    }

    @Nested
    inner class RecoverTest {
        @Test
        fun `성공한 Future는 recover를 수행하지 않는다`() {
            success.recover { 42 }.get() shouldBeEqualTo 1

            success.recover { throw RuntimeException() }.get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future에 대해서 recover가 수행된다`() {
            failed.recover { 42 }.get() shouldBeEqualTo 42
        }

        @Test
        fun `실패한 Future에 recover 내부에서도 예외가 발생하면 recover는 실패한다`() {
            assertThrows<Exception> {
                failed.recover { throw RuntimeException() }.get()
            }.cause shouldBeInstanceOf RuntimeException::class
        }
    }

    @Nested
    inner class RecoverWithTest {
        @Test
        fun `성공한 Future는 recoverWith를 실행할 필요가 없습니다`() {
            success.recoverWith { 42.asCompletableFuture() }.get() shouldBeEqualTo 1

            success.recoverWith { throw RuntimeException() }.get() shouldBeEqualTo 1
            success
                .recoverWith { 42.asCompletableFuture() }
                .recoverWith { throw RuntimeException() }.get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 recoverWith를 샐행합니다`() {
            failed.recoverWith { 42.asCompletableFuture() }.get() shouldBeEqualTo 42

            failed
                .recoverWith { RuntimeException().asCompletableFuture() }
                .recoverWith { 42.asCompletableFuture() }
                .get() shouldBeEqualTo 42
        }

        @Test
        fun `실패한 Future 를 Exception으로 recover 합니다`() {
            assertThrows<Exception> {
                failed.recoverWith { RuntimeException().asCompletableFuture() }.get()
            }
        }
    }

    @Nested
    inner class FallbackTest {

        @Test
        fun `성공한 Future는 fallback를 실행할 필요가 없다`() {
            success.fallback { 42 }.get() shouldBeEqualTo 1
        }

        @Test
        fun `예외가 발생한 경우 fallback를 실행한다`() {
            failed.fallback { 42 }.get() shouldBeEqualTo 42
        }

        @Test
        fun `예외가 발생한 경우 fallback에서도 예외가 발생하면 예외로 처리된다`() {
            val error = assertThrows<Exception> {
                failed.fallback { throw RuntimeException() }.get()
            }
            error.cause shouldBeInstanceOf RuntimeException::class
        }
    }

    @Nested
    inner class FallbackToTest {

        @Test
        fun `성공한 Future는 fallbackTo를 실행할 필요가 없다`() {
            success.fallbackTo { 42.asCompletableFuture() }.get() shouldBeEqualTo 1
        }

        @Test
        fun `예외가 발생한 경우 fallbackTo를 실행한다`() {
            failed.fallbackTo { 42.asCompletableFuture() }.get() shouldBeEqualTo 42
        }

        @Test
        fun `예외가 발생한 경우 fallbackTo에서도 예외가 발생하면 예외로 처리된다`() {
            val error = assertThrows<Exception> {
                failed.fallbackTo { RuntimeException().asCompletableFuture() }.get()
            }
            error.cause shouldBeInstanceOf RuntimeException::class
        }
    }

    @Nested
    inner class MapErrorTest {

        @Test
        fun `성공한 Future는 mapError를 실행할 필요가 없습니다`() {
            success.mapError<Int, IllegalArgumentException> { IllegalStateException() }.get() shouldBeEqualTo 1
        }

        @Test
        fun `예외 처리가 가능한 경우 mapError를 실행한다`() {
            val error = assertThrows<Exception> {
                failed.mapError<Int, IllegalArgumentException> { UnsupportedOperationException() }.get()
            }
            error.cause shouldBeInstanceOf UnsupportedOperationException::class
        }

        @Test
        fun `예외 타입이 처리가 불가한 경우 mapError는 실행되지 않는다`() {
            val error = assertThrows<Exception> {
                failed.mapError<Int, IOException> { UnsupportedOperationException() }.get()
            }
            error.cause shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `예외 타입이 Super type인 경우 mapError가 실행된다`() {
            val error = assertThrows<Exception> {
                failed.mapError<Int, Throwable> { UnsupportedOperationException() }.get()
            }
            error.cause shouldBeInstanceOf UnsupportedOperationException::class
        }
    }

    @Nested
    inner class CallbackTest {

        @Test
        fun `성공한 Future는 onFailure를 호출하지 않습니다`() {
            success.onFailure { fail("성공한 Future는 onFailure가 발생하지 않습니다.") }.get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 onFailure를 호출합니다`() {
            var capturedError: Throwable? = null
            failed.onFailure { capturedError = it }.recover { 1 }.get()
            capturedError shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `성공한 Future는 onSuccess 를 호출합니다`() {
            var capturedResult: Any? = null
            success.onSuccess { capturedResult = it }.get()
            capturedResult shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 onSuccess를 호출하지 않습니다`() {
            failed.onSuccess { fail("onSuccess를 호출하면 안됩니다") }.recover { 1 }.get()
        }

        @Test
        fun `성공한 Future에 대한 onComplete 호출`() {
            var capturedResult = 0
            success.onComplete(
                onFailure = { fail("호출되면 안됩니다") },
                onSuccess = { capturedResult = it }
            ).get()

            capturedResult shouldBeEqualTo success.get()
        }

        @Test
        fun `실패한 Future에 대한 onComplete 호출`() {
            var capturedError: Throwable? = null

            val result: Int = failed.onComplete(
                onFailure = { capturedError = it },
                onSuccess = { fail("호출되면 안됩니다") }
            ).recover { 42 }.get()

            capturedError shouldBeInstanceOf IllegalArgumentException::class
            result shouldBeEqualTo 42
        }
    }

    @Nested
    inner class ZipTest {

        @Test
        fun `성공한 Future에 대한 zip`() {
            success.zip(success).get() shouldBeEqualTo (1 to 1)
            success.zip(immediateFuture { "Hello" }).get() shouldBeEqualTo (1 to "Hello")
        }

        @Test
        fun `실패한 Future에 대한 zip`() {
            assertThrows<Exception> { failed.zip(success).get() }
            assertThrows<Exception> { failed.zip(failed).get() }
            assertThrows<Exception> { success.zip(failed).get() }
        }

        @Test
        fun `성공한 future에 대한 zip 과 lambda`() {
            success.zip(success) { a, b -> a + b }.get() shouldBeEqualTo 2
            success.zip(futureOf { "Hello" }) { a, b -> a.toString() + b }.get() shouldBeEqualTo "1Hello"
            success.zip(immediateFuture { "Hello" }) { a, b -> a.toString() + b }.get() shouldBeEqualTo "1Hello"
        }

        @Test
        fun `실패한 future에 대한 zip과 lambda`() {
            assertThrows<Exception> {
                failed.zip(failed) { a, b -> a + b }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class

            assertThrows<Exception> {
                failed.zip(success) { a, b -> a + b }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class

            assertThrows<Exception> {
                success.zip(failed) { a, b -> a + b }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }
    }

    @Nested
    inner class FirstCompletionTest {

        @Test
        fun `성공 케이스`() {
            val f3 = futureOf { Thread.sleep(3000); 3 }
            val f2 = futureOf { Thread.sleep(2000); 2 }
            val f1 = immediateFuture { 1 }

            listOf(f3, f2, f1).futureFirst().get() shouldBeEqualTo 1

        }

        @Test
        fun `첫번째에 실패가 나는 경우`() {
            val f3 = futureOf { Thread.sleep(3000); 3 }
            val f2 = futureOf { Thread.sleep(2000); 2 }
            val f1 = immediateFuture<Int> { throw IllegalArgumentException() }

            assertThrows<Exception> {
                listOf(f3, f2, f1).futureFirst().get()
            }
        }

        @Test
        fun `첫번째가 예외가 아닌 경우`() {
            val f3 = futureOf { Thread.sleep(3000); 3 }
            val f2 = futureOf<Int> { Thread.sleep(2000); throw IllegalArgumentException() }
            val f1 = immediateFuture { 1 }

            listOf(f3, f2, f1).futureFirst().get() shouldBeEqualTo 1
        }
    }

    @Nested
    inner class FutureListTest {

        @Test
        fun `Future List를 List의 Future로 변환하기`() {
            val list = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                futureOf { Thread.sleep(100); 3 })

            list.futureFlatten().get() shouldContainSame listOf(1, 2, 3)
        }

        @Test
        fun `Future List에 실패한 Future가 있는 경우 List의 Future로 변환하기`() {
            val list = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                IllegalArgumentException().asCompletableFuture())

            assertThrows<Exception> {
                list.futureFlatten().get()
            }
        }

        @Test
        fun `Future List에서 mapping 하기`() {
            val list = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                futureOf { Thread.sleep(100); 3 })

            list.futureMap { it * it }.get() shouldContainSame listOf(1, 4, 9)
        }

        @Test
        fun `Future List에서 실패한 Future가 있는 경우 List의 Future로 변환하기에서 예외`() {
            val list = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                IllegalArgumentException().asCompletableFuture())

            assertThrows<Exception> {
                list.futureMap { it * it }.get()
            }
        }

        @Test
        fun `Future List에서 성공한 Future만 가져오기`() {
            val list = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                IllegalArgumentException().asCompletableFuture())

            list.futureSuccessful().get() shouldContainSame listOf(1, 2)
        }

        @Test
        fun `Future List가 모두 실패하는 경우`() {
            val list = listOf(failed, failed, failed)
            list.futureSuccessful().get() shouldContainSame emptyList()
        }
    }

    @Nested
    inner class FutureListFoldTest {

        @Test
        fun `성공한 Future List를 fold 하기`() {
            val list = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                futureOf { Thread.sleep(100); 3 })

            list.futureFold(0) { acc, it -> acc + it }.get() shouldBeEqualTo 6
        }

        @Test
        fun `실패한 Future가 있는 List에 fold 하기`() {
            val list = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                IllegalArgumentException().asCompletableFuture())

            assertThrows<Exception> {
                list.futureFold(0) { acc, it -> acc + it }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `빈 Future List를 fold 하기`() {
            emptyList<CompletableFuture<Int>>()
                .futureFold(0) { acc, i -> acc + i }
                .get() shouldBeEqualTo 0
        }
    }

    @Nested
    inner class FutureListReduceTest {

        @Test
        fun `성공한 Future List를 reduce 하기`() {
            val futures = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                futureOf { Thread.sleep(100); 3 })

            futures.futureReduce { acc, it -> acc + it }.get() shouldBeEqualTo 6
        }

        @Test
        fun `실패한 Future가 있는 List에 reduce 하기`() {
            val futures = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                IllegalArgumentException().asCompletableFuture())

            assertThrows<Exception> {
                futures.futureReduce { acc, it -> acc + it }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `빈 List reduce 하기`() {
            assertThrows<NoSuchElementException> {
                emptyList<CompletableFuture<Int>>().futureReduce { acc, it -> acc + it }.get()
            }
        }
    }

    @Nested
    inner class FutureListMapTest {

        @Test
        fun `성공한 Future List를 map 하기`() {
            val futures: List<CompletableFuture<Int>> = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                futureOf { Thread.sleep(100); 3 })

            futures.futureMap { it * it }.get() shouldContainSame listOf(1, 4, 9)
        }

        @Test
        fun `실패한 Future가 있는 List에 map 하기`() {
            val futures = listOf(
                1.asCompletableFuture(),
                2.asCompletableFuture(),
                IllegalArgumentException().asCompletableFuture())

            assertThrows<Exception> {
                futures.futureMap { it * it }.get()
            }
        }

        @Test
        fun `빈 List map 하기`() {
            emptyList<CompletableFuture<Int>>().futureMap { it * it }.get().shouldBeEmpty()
        }
    }
}