package io.github.debop.springdata.jdbc.basic

import mu.KLogging

/**
 * Output
 * @author debop (Sunghyouk Bae)
 */
object Output : KLogging() {

    fun list(categories: Iterable<*>, title: String) {
        val message = StringBuilder("==== $title ====\n")

        categories.forEach { category ->
            message.append(category.toString().replace(", ", ",\n\t"))
        }
        logger.info(message.toString())
    }
}