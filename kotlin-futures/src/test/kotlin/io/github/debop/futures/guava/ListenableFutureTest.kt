package io.github.debop.futures.guava

import com.google.common.util.concurrent.ListenableFuture
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import java.io.IOException

/**
 * ListenableFutureTest
 *
 * @author debop
 * @since 19. 7. 15
 */
class ListenableFutureTest {

    val success = 1.asListenableFuture()
    val failed = IllegalArgumentException().asListenableFuture<Int>()

    @Nested
    inner class MapTest {

        @Test
        fun `성공한 Future는 map을 수행합니다`() {
            success.map { it + 1 }.get() shouldBeEqualTo 2
        }

        @Test
        fun `실패한 Future는 map 실행 시 자신의 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.map { it + 1 }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }
    }

    @Nested
    inner class FlatMapTest {

        @Test
        fun `성공한 Future는 flatMap을 수행합니다`() {
            success.flatMap { it.asListenableFuture() }.get() shouldBeEqualTo success.get()
            success.flatMap { immediateFuture { it + 1 } }.get() shouldBeEqualTo 2
        }

        @Test
        fun `실패한 Future는 flatMap 수행 시 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.flatMap { immediateFuture { it + 1 } }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }
    }

    @Nested
    inner class FilterTest {

        @Test
        fun `성공한 Future이고 filter를 만족하면 결과를 반환한다`() {
            success.filter { it == 1 }.get() shouldBeEqualTo 1
        }

        @Test
        fun `성공한 Future지만 filter를 만족하지 못하면 NoSuchElementException을 발생시킨다`() {
            assertThrows<Exception> {
                success.filter { it == 42 }.get()
            }.cause shouldBeInstanceOf NoSuchElementException::class
        }

        @Test
        fun `실패한 Future는 filter 시 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.filter { it == 1 }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }
    }

    @Nested
    inner class RecoverTest {

        @Test
        fun `성공한 Future는 recover를 수행하지 않습니다`() {
            success.recover { 42 }.get() shouldBeEqualTo 1

            success.recover { throw RuntimeException() }.get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 recover를 수행합니다`() {
            failed.recover { 42 }.get() shouldBeEqualTo 42
        }

        @Test
        fun `recover에서 예외 발생 시에는 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.recover { throw RuntimeException() }.get()
            }.cause shouldBeInstanceOf RuntimeException::class
        }
    }

    @Nested
    inner class RecoverWithTest {

        @Test
        fun `성공한 Future는 recoverWith를 수행하지 않습니다`() {
            success.recoverWith { 42.asListenableFuture() }.get() shouldBeEqualTo 1
            success.recoverWith { throw RuntimeException() }.get() shouldBeEqualTo 1

            success
                .recoverWith { 42.asListenableFuture() }
                .recoverWith { throw RuntimeException() }
                .get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 recoverWith를 실행합니다`() {
            failed.recoverWith { 42.asListenableFuture() }.get() shouldBeEqualTo 42

            failed
                .recoverWith { RuntimeException().asListenableFuture() }
                .recoverWith { 42.asListenableFuture() }
                .get() shouldBeEqualTo 42
        }

        @Test
        fun `실패한 Future recoverWith에서 예외를 발생시키면 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.recoverWith { RuntimeException().asListenableFuture() }.get()
            }.cause shouldBeInstanceOf RuntimeException::class
        }
    }

    @Nested
    inner class FallbackTest {

        @Test
        fun `성공한 Future는 fallback를 실행하지 않습니다`() {
            success.fallback { 42 }.get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 fallback을 실행한다`() {
            failed.fallback { 42 }.get() shouldBeEqualTo 42
        }

        @Test
        fun `실패한 Future에 fallback에서도 예외가 발생하면 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.fallback { throw RuntimeException() }.get()
            }.cause shouldBeInstanceOf RuntimeException::class
        }
    }

    @Nested
    inner class FallbackToTest {

        @Test
        fun `성공한 Future는 fallbackTo를 실행하지 않습니다`() {
            success.fallbackTo { 42.asListenableFuture() }.get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 fallbackTo을 실행한다`() {
            failed.fallbackTo { 42.asListenableFuture() }.get() shouldBeEqualTo 42
        }

        @Test
        fun `실패한 Future에 fallbackTo에서도 예외가 발생하면 예외를 발생시킨다`() {
            assertThrows<Exception> {
                failed.fallbackTo { RuntimeException().asListenableFuture() }.get()
            }.cause shouldBeInstanceOf RuntimeException::class
        }
    }

    @Nested
    inner class MapErrorTest {

        @Test
        fun `성공한 Future는 mapError를 실행할 필요가 없습니다`() {
            success.mapError { _: IllegalArgumentException -> IllegalStateException() }.get() shouldBeEqualTo 1
        }

        @Test
        fun `예외 처리가 가능한 경우 mapError를 실행한다`() {
            assertThrows<Exception> {
                failed.mapError { _: IllegalArgumentException -> UnsupportedOperationException() }.get()
            }.cause shouldBeInstanceOf UnsupportedOperationException::class
        }

        @Test
        fun `예외 처리가 불가능한 경우 mapError는 실행되지 않습니다`() {
            assertThrows<Exception> {
                failed.mapError { _: IOException -> UnsupportedOperationException() }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `예외 타입이 Super type인 경우 mapError가 실행됩니다`() {
            assertThrows<Exception> {
                failed.mapError { _: Throwable -> UnsupportedOperationException() }.get()
            }.cause shouldBeInstanceOf UnsupportedOperationException::class
        }
    }

    /**
     * NOTE: Callback 시에는 [ForkJoinExecutor] 를 쓰면 안되고, [DirectExecutor]를 사용해야만 제대로 작동됩니다.
     *
     */
    @Nested
    inner class CallbackTest {

        @Test
        fun `성공한 Future는 onFailure를 호출하지 않습니다`() {
            success.onFailure(DirectExecutor) { fail("호출하면 안됩니다") }.get() shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 onFailure를 호출합니다`() {
            var capturedError: Throwable? = null
            failed.onFailure(DirectExecutor) { capturedError = it }.recover { 1 }.get()
            capturedError shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `성공한 Future는 onSuccess를 호출합니다`() {
            var capturedResult: Any? = null
            success.onSuccess(DirectExecutor) { capturedResult = it }.get()
            capturedResult shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future는 onSuccess를 호출하지 않습니다`() {
            failed.onSuccess(DirectExecutor) { fail("호출하지 않습니다") }.recover { 1 }.get()
        }

        @Test
        fun `성공한 Future에 대한 onComplete 호출`() {
            var capturedResult: Any? = null
            success.onComplete(DirectExecutor,
                               onFailure = { fail("호출하지 않습니다") },
                               onSuccess = { capturedResult = it }
            ).get()

            capturedResult shouldBeEqualTo 1
        }

        @Test
        fun `실패한 Future에 대한 onComplete 호출`() {
            var capturedError: Throwable? = null
            var result = failed.onComplete(DirectExecutor,
                                           onFailure = { capturedError = it },
                                           onSuccess = { fail("호출하지 않습니다") }
            )
                .recover { 42 }
                .get()

            capturedError shouldBeInstanceOf IllegalArgumentException::class
            result shouldBeEqualTo 42
        }
    }

    @Nested
    inner class ZipTest {

        @Test
        fun `성공한 Future 들에 대한 zip operation`() {
            success.zip(success).get() shouldBeEqualTo (1 to 1)
            success.zip(immediateFuture { "Hello" }).get() shouldBeEqualTo (1 to "Hello")
        }

        @Test
        fun `실패한 Future가 있는 zip operation`() {
            assertThrows<Exception> { failed.zip(failed).get() }
            assertThrows<Exception> { failed.zip(success).get() }
            assertThrows<Exception> { success.zip(failed).get() }
        }

        @Test
        fun `성공한 Future 들에 대한 zip과 map`() {
            success.zip(success) { a, b -> a + b }.get() shouldBeEqualTo 2
            success.zip(listenableFuture { "Hello" }) { a, b -> a.toString() + b }.get() shouldBeEqualTo "1Hello"
            success.zip(immediateFuture { "Hello" }) { a, b -> a.toString() + b }.get() shouldBeEqualTo "1Hello"
        }

        @Test
        fun `실패한 Future가 있는 zip과 map`() {
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
    inner class FutureListFoldTest {

        @Test
        fun `성공한 Future List를 fold하기`() {
            val futures = listOf(
                1.asListenableFuture(),
                2.asListenableFuture(),
                listenableFuture { Thread.sleep(100); 3 }
            )
            futures.futureFold(0) { acc, it -> acc + it }.get() shouldBeEqualTo (1 + 2 + 3)
        }

        @Test
        fun `실패가 있는 Future List를 fold하기`() {
            val futures = listOf(
                1.asListenableFuture(),
                2.asListenableFuture(),
                IllegalArgumentException().asListenableFuture<Int>()
            )
            assertThrows<Exception> {
                futures.futureFold(0) { acc, it -> acc + it }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `빈 Future List를 fold 하기`() {
            emptyList<ListenableFuture<Int>>().futureFold(0) { acc, it -> acc + it }.get() shouldBeEqualTo 0
        }
    }

    @Nested
    inner class FutureListReduceTest {

        @Test
        fun `성공한 Future List를 reduce 하기`() {
            val futures = listOf(
                1.asListenableFuture(),
                2.asListenableFuture(),
                listenableFuture { Thread.sleep(100); 3 }
            )
            futures.futureReduce { acc, i -> acc + i }.get() shouldBeEqualTo (1 + 2 + 3)
        }

        @Test
        fun `실패한 Future가 있는 List를 reduce하기`() {
            val futures = listOf(
                1.asListenableFuture(),
                2.asListenableFuture(),
                IllegalArgumentException().asListenableFuture<Int>()
            )
            assertThrows<Exception> {
                futures.futureReduce { acc, it -> acc + it }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `빈 Future List를 reduce하기`() {
            assertThrows<NoSuchElementException> {
                emptyList<ListenableFuture<Int>>().futureReduce { acc, i -> acc + i }.get()
            }
        }
    }

    @Nested
    inner class FutureListMapTest {

        @Test
        fun `성공한 Future List를 map 하기`() {
            val futures = listOf(
                1.asListenableFuture(),
                2.asListenableFuture(),
                listenableFuture { Thread.sleep(100); 3 }
            )

            futures.futureMap { it * it }.get() shouldContainSame listOf(1, 4, 9)
        }

        @Test
        fun `실패한 Future가 있는 List를 map 하기`() {
            val futures = listOf(
                1.asListenableFuture(),
                2.asListenableFuture(),
                IllegalArgumentException().asListenableFuture<Int>()
            )
            assertThrows<Exception> {
                futures.futureMap { it * it }.get()
            }.cause shouldBeInstanceOf IllegalArgumentException::class
        }

        @Test
        fun `빈 Future List 를 map 하기`() {
            emptyList<ListenableFuture<Int>>().futureMap { it * it }.get().shouldBeEmpty()
        }
    }
}