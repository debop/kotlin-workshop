package io.github.debop.kotlin.workshop.utils


fun Int.toByteArray(): ByteArray =
    List(4) { (this ushr (8 * it)).toByte() }.toByteArray()

fun Long.toByteArray(): ByteArray =
    List(8) { (this ushr (8 * it)).toByte() }.toByteArray()

fun ByteArray.toInt(): Int =
    List(4) { (this[it].toInt() and 0xFF) shl (8 * it) }
        .reduce { num, it -> num or it }

fun ByteArray.toLong(): Long =
    List(8) { (this[it].toLong() and 0xFF) shl (8 * it) }
        .reduce { num, it -> num or it }
