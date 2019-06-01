package io.github.debop.kotlin.tests.extensions

import org.junit.jupiter.api.extension.ExtendWith

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS,
        AnnotationTarget.FILE,
        AnnotationTarget.FUNCTION)
@MustBeDocumented
@ExtendWith(RandomBeansExtension::class)
annotation class Randomized