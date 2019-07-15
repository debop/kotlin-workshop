package org.javers.core

import org.javers.common.date.DateProvider
import java.time.ZonedDateTime

/**
 * TikDateProvider
 *
 * @author debop
 * @since 19. 7. 15
 */
class TikDateProvider(private var dateTime: ZonedDateTime = ZonedDateTime.now()): DateProvider {

    override fun now(): ZonedDateTime {
        val now = dateTime
        dateTime = dateTime.plusSeconds(1)
        return now
    }

    fun set(now: ZonedDateTime) {
        this.dateTime = now
    }
}