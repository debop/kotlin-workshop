package io.github.debop.kotlin.tests.extensions

/**
 * DomainObject
 * @author debop (Sunghyouk Bae)
 */
class DomainObject {

    var id: Int = 0
    var name: String? = null
    var value: Long = 0L
    var price: Double = 0.0
    val nestedDomainObject: NestedDomainObject? = null
    val wotsits: List<String>? = null


    class NestedDomainObject {
        var address: String? = null
        var category: String? = null
    }
}