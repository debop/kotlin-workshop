package io.github.debop.jackson.dataformat.yaml

/**
 * Yaml 형태의 문자열에서 yaml 의 document marker 인 `---` 를 제거한다
 *
 * @return yaml의 documednt marker를 제거한 문자열
 */
fun String.trimYamlDocMarker(): String {
    var doc = this
    if (startsWith("---")) {
        doc = doc.substring(3)
    }
    return doc.trim()
}