package io.github.debop.kotlin.tests.extensions

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
annotation class Random(val excludes: Array<String> = [],
                        val size: Int = 10,
                        val type: KClass<*> = Any::class)