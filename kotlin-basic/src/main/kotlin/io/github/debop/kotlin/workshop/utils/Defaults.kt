package io.github.debop.kotlin.workshop.utils

import java.nio.charset.Charset
import java.util.Locale

/**
 * `object` 를 이용해서 static method를 제공합니다.
 *
 * @author debop (Sunghyouk Bae)
 */
object Defaults {

    @JvmField
    val DefaultCharset: Charset = Charsets.UTF_8

    @JvmStatic
    val DefaultLocale: Locale by lazy { Locale.getDefault() }

    @JvmStatic
    val ProcessCount: Int by lazy { Runtime.getRuntime().availableProcessors() }

}