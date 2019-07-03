package io.github.debop.kotlin.workshop.lazy

import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

class FutureValueTest {

    @Test
    fun `CompletableFuture를 이용한 비동기 값 계산`() {
        val time = System.nanoTime()

        val futureVal = FutureValue {
            Thread.sleep(100L)
            time
        }

        futureVal.isDone.shouldBeFalse()

        Thread.sleep(200)

        futureVal.isDone.shouldBeTrue()
        futureVal.value shouldEqualTo time
    }
}