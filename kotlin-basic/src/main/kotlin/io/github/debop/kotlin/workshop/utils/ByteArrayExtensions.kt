package io.github.debop.kotlin.workshop.utils

/**
 * [ByteArray] 정보를 `dest` 로 복사합니다.
 *
 * @param dest
 */
fun ByteArray.copyTo(dest: ByteArray) {
    System.arraycopy(this, 0, dest, 0, dest.size)
}

fun ByteArray.copyFrom(src: ByteArray) {
    System.arraycopy(src, 0, this, 0, this.size)
}