package io.github.debop.kotlin.workshop.annotation

/**
 * Kotlin Data Class 등에 NoArgConstructor를 추가로 만들 수 있도록 하기 위해
 * kotlin noArg plugin 과 같이 사용할 수 있습니다.
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 15
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KotlinValueClass