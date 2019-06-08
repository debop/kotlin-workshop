package io.github.debop.exception

import java.util.function.Predicate

/**
 * RecordFailurePredicate
 * @author debop (Sunghyouk Bae)
 */
class RecordFailurePredicate : Predicate<Throwable> {
    override fun test(throwable: Throwable): Boolean {
        return throwable !is BusinessException
    }
}