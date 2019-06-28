package io.github.debop.jackson.dataformat.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import mu.KLogging

/**
 * AbstractYamlExample
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 27
 */
abstract class AbstractYamlExample {

    companion object: KLogging()

    val yamlMapper by lazy { YAMLMapper().apply { registerKotlinModule() } }
    val yamlFactory by lazy { YAMLFactory() }
    val objectMapper by lazy { ObjectMapper().registerKotlinModule() }

}