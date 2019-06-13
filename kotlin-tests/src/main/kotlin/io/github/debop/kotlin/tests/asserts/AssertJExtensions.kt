package io.github.debop.kotlin.tests.asserts

import org.assertj.core.api.AbstractBooleanAssert
import org.assertj.core.api.Assertions
import org.assertj.core.api.ObjectAssert


fun Boolean?.isTrue(): AbstractBooleanAssert<*> =
    Assertions.assertThat(this).isTrue()

fun Boolean?.isFalse(): AbstractBooleanAssert<*> =
    Assertions.assertThat(this).isFalse()

infix fun <T> T.isEqualTo(expected: T): ObjectAssert<T> =
    Assertions.assertThat(this).isEqualTo(expected)
