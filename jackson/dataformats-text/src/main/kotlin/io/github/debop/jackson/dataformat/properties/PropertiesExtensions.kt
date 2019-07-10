package io.github.debop.jackson.dataformat.properties

/**
 * PropertiesExtensions
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */

/**
 * [Properties] 형태의 정보를 파싱할 때, [path]의 종단에 해당하는 하위 노드를 가져온다
 *
 * @param path
 * @param delimiter
 * @return
 */
@Suppress("UNCHECKED_CAST")
@JvmOverloads
fun Map<Any, Any>.getNode(path: String, delimiter: String = "."): Map<Any, Any> {
    val nodes = path.split(delimiter)
    var map = this

    nodes.forEach {
        map = map[it] as Map<Any, Any>
    }
    return map
}