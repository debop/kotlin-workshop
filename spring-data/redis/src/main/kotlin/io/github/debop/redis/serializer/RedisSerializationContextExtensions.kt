package io.github.debop.redis.serializer

import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


fun redisSerializationContextOf(valueSerializer: RedisSerializer<Any>): RedisSerializationContext<String, Any> {
    return RedisSerializationContext
        .newSerializationContext<String, Any>()
        .key(StringRedisSerializer.UTF_8)
        .value(valueSerializer)
        .build()
}

fun <K: Any, V: Any> redisSerializationContextOf(
    keySerializer: RedisSerializer<K>,
    valueSerializer: RedisSerializer<V>): RedisSerializationContext<K, V> {

    return RedisSerializationContext
        .newSerializationContext<K, V>()
        .key(keySerializer)
        .value(valueSerializer)
        .build()
}