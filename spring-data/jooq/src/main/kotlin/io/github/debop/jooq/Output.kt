package io.github.debop.jooq

import mu.KLogging

/**
 * Output
 * @author debop (Sunghyouk Bae)
 */
object Output : KLogging() {

    fun list(categories: Iterable<*>, title: String) {
        val message = StringBuilder("==== $title ====\n")

        categories.forEach {
            message.appendln(it?.toString()?.replace(", ", ",\n\t") ?: "empty")
        }

        logger.info { message.toString() }
    }

}