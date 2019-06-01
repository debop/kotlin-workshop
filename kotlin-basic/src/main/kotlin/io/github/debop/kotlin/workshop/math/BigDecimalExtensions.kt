package io.github.debop.kotlin.workshop.math

import java.math.BigDecimal

// Convert numbers to [BigDecimal]

fun Number.toBigDecimal(): BigDecimal = if (this is BigDecimal) this else BigDecimal(this.toString())

//
// BigDecimal basic operators
//

operator fun BigDecimal.plus(other: Number): BigDecimal =
    this.add(other.toBigDecimal())

operator fun BigDecimal.minus(other: Number): BigDecimal =
    this.subtract(other.toBigDecimal())

operator fun BigDecimal.times(other: Number): BigDecimal =
    this.multiply(other.toBigDecimal())

operator fun BigDecimal.div(other: Number): BigDecimal =
    this.divide(other.toBigDecimal())

operator fun Number.times(other: BigDecimal): BigDecimal =
    other.multiply(this.toBigDecimal())

operator fun Number.div(other: BigDecimal): BigDecimal =
    other.divide(this.toBigDecimal())

operator fun BigDecimal.compareTo(other: Number): Int = this.compareTo(other.toBigDecimal())

