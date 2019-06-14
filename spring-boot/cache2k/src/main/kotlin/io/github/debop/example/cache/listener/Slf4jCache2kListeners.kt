package io.github.debop.example.cache.listener

import org.cache2k.Cache
import org.cache2k.CacheEntry
import org.cache2k.event.CacheEntryCreatedListener
import org.slf4j.LoggerFactory


internal val cacheEventLogger: org.slf4j.Logger = LoggerFactory.getLogger("cache2KListener")

open class Slf4jCacheEntryCreatedListener<K, V>(val logger: org.slf4j.Logger = cacheEventLogger) : CacheEntryCreatedListener<K, V> {
    override fun onEntryCreated(cache: Cache<K, V>, entry: CacheEntry<K, V>) {
        logger.info("Cache entry created: key=${entry.key}, value=${entry.value}")
    }
}