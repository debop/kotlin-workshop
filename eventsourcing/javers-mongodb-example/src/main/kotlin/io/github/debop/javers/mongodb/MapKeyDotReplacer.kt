package io.github.debop.javers.mongodb

import mu.KLogging
import org.bson.Document

/**
 * MapKeyDotReplacer
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 7. 13
 */
class MapKeyDotReplacer {

    companion object: KLogging() {
        private const val REPLACEMENT = "#dot#"
    }

    internal fun replaceInSnapshotState(snapshot: Document): Document {
        return replaceInPropertyMaps(snapshot, "\\.", ".", REPLACEMENT)
    }

    internal fun back(snapshot: Document): Document {
        return replaceInPropertyMaps(snapshot, REPLACEMENT, REPLACEMENT, ".")
    }

    private fun replaceInPropertyMaps(snapshot: Document, regexFrom: String, from: String, to: String): Document {
        val state = getState(snapshot)

        state.keys.forEach { key ->
            val mapProperty = state[key]
            if (mapProperty is Document) {
                state[key] = replaceInMapKeys(mapProperty, regexFrom, from, to)
            }
        }

        return snapshot
    }

    private fun getState(snapshot: Document): Document {
        return snapshot.get("state") as Document
    }

    private fun replaceInMapKeys(map: Document, regexFrom: String, from: String, to: String): Document {
        map.keys.forEach { key ->
            if (key.contains(from)) {
                val escaped = key.replace(regexFrom, to)
                val value = map[key]
                map.remove(key)
                map[escaped] = value
            }
        }
        return map
    }
}