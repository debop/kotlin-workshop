package org.javers.core.model

import org.javers.core.metamodel.annotation.Id


enum class SomeEnum { A, B }

data class PrimitiveEntity(@Id val id: String = "a") {

    var intField: Int = 0
    var longField: Long = 0L
    var doubleField: Double = 0.0
    var floatField: Float = 0.0F
    var charField: Char = 0.toChar()
    var byteField: Byte = 0.toByte()
    var shortField: Short = 0.toShort()
    var booleanField: Boolean = false

    var IntegerField: Int? = null
    var LongField: Long? = null
    var DoubleField: Double? = null
    var FloatField: Float? = null
    var CharField: Char? = null
    var ByteField: Byte? = null
    var ShortField: Short? = null
    var BooleanField: Boolean? = null

    var someEnum: SomeEnum? = null

}