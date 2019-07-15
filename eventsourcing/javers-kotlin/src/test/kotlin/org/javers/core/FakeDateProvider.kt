package org.javers.core

import org.javers.common.date.DateProvider
import java.time.ZonedDateTime

/**
 * FakeDateProvider
 *
 * @author debop
 * @since 19. 7. 15
 */
class FakeDateProvider(private var dateTime: ZonedDateTime? = null): DateProvider {

    override fun now(): ZonedDateTime = dateTime ?: ZonedDateTime.now()

    fun set(dateTime: ZonedDateTime?) {
        this.dateTime = dateTime
    }
}