package io.github.debop.kotlin.workshop.utils


// Same as : str ?: fallback
inline fun String?.ifNull(fallback: () -> String): String = when (this) {
    null -> fallback()
    else -> this
}

inline fun String?.ifNullOrEmpty(fallback: () -> String): String = when {
    this.isNullOrEmpty() -> fallback()
    else -> this
}

inline fun String?.ifNullOrBlank(fallback: () -> String): String = when {
    this.isNullOrBlank() -> fallback()
    else -> this
}
