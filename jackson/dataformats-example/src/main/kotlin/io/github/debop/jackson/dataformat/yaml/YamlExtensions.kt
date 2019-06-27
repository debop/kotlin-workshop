package io.github.debop.jackson.dataformat.yaml

/**
 * YamlExtensions
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */

fun String.trimYamlDocMarker(): String {
    var doc = this
    if(startsWith("---")) {
        doc = doc.substring(3)
    }
    return doc.trim()
}