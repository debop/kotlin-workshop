package io.github.debop.exception

/**
 * BusinessException
 * @author debop (Sunghyouk Bae)
 */
class BusinessException : RuntimeException {

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)

}