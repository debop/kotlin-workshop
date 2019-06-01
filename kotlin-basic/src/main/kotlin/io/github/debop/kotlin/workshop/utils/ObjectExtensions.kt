package io.github.debop.kotlin.workshop.utils

import java.io.Closeable
import java.util.Optional

/**
 * var 로 선언된 필드 중 non null 수형에 대해 초기화 값을 지정하고자 할 때 사용합니다.
 * 특히 ```@Autowired```, ```@Inject``` val 수형에 사용하기 좋다.
 *
 * <pre>
 *   <code>
 *      @Inject val x: Repository = uninitialized()
 *   </code>
 * </pre>
 * @see lateinit
 * @see Delegates.nonNull
 */
@Suppress("UNCHECKED_CAST")
fun <T> uninitialized(): T = null as T

fun <T : Any> T?.asOptional(): Optional<T> = Optional.ofNullable(this)


fun Closeable.closeQuietly() {
    try {
        close()
    } catch (e: Exception) {
        // Nothing to do
    }
}

@SinceKotlin("1.3")
fun Closeable.closeSilence() {
    runCatching { close() }
}