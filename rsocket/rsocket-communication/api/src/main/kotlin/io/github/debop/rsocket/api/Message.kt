package io.github.debop.rsocket.api

import java.time.Instant

/**
 * Message
 *
 * @author debop
 * @since 2020/11/25
 */
data class Message @JvmOverloads constructor(
    var origin: String = "",
    var interaction: String = "",
    var index: Long = 0,
) {
    val created = Instant.now().epochSecond
}