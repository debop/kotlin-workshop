package io.github.debop.kotlin.workshop.lazy

import java.io.Serializable

/**
 * Lazy initialized value wrapper
 *
 * @author debop (Sunghyouk Bae)
 */
class LazyValue<out T: Any>(private inline val initializer: () -> T): Serializable {

    private var initialized: Boolean = false

    val isInitialized: Boolean get() = initialized

    val value: T by lazy {
        initialized = true
        initializer.invoke()
    }

    fun <S : Any> map(mapper: (T) -> S): LazyValue<S> {
        return LazyValue { mapper.invoke(this.value) }
    }

    fun <S : Any> flatMap(mapper: (T) -> LazyValue<S>): LazyValue<S> {
        return LazyValue { mapper.invoke(this.value).value }
    }

}