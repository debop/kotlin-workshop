package io.github.debop.jackson.dataformat.properties

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import mu.KLogging

/**
 * AbstractPropertiesExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
abstract class AbstractPropertiesExample {

    companion object: KLogging()

    val propsMapper: JavaPropsMapper by lazy { JavaPropsMapper().apply { registerKotlinModule() } }

    val propsFactory: JavaPropsFactory by lazy { JavaPropsFactory() }

    val objectMapper: ObjectMapper by lazy { ObjectMapper().registerKotlinModule() }

}