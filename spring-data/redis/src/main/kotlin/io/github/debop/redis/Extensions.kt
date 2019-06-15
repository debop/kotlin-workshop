package io.github.debop.redis

import org.springframework.data.redis.util.ByteUtils
import java.nio.ByteBuffer

fun ByteBuffer.toUtf8String(): String =
    ByteUtils.getBytes(this).toString(Charsets.UTF_8)

fun String.toByteBuffer(): ByteBuffer =
    ByteBuffer.wrap(this.toByteArray())