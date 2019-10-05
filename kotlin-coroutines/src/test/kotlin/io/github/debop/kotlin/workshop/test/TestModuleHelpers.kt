package io.github.debop.kotlin.workshop.test

import kotlinx.coroutines.CoroutineScope
import java.time.Instant

const val SLOW: Long = 10_000L

/**
 * `block`을 실행하는데, 실행 시간이 2초 이상이 되면 예외를 발생시킨다.
 * [runBlockingTest]이 delay를 무시하는지 테스트하기 위해 사용합니다.
 */
suspend fun CoroutineScope.assertRunsFast(block: suspend CoroutineScope.() -> Unit) {

    val start = Instant.now().toEpochMilli()

    // don't need to be fancy with timeouts here since anything longer than a few ms is an error
    block()

    val duration = Instant.now().minusMillis(start).toEpochMilli()

    check(duration < 2_000) {
        "All tests must complete within 2000ms (use longer timeouts to cause failure)"
    }
}