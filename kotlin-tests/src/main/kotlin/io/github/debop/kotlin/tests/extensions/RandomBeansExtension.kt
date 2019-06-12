package io.github.debop.kotlin.tests.extensions

import com.esotericsoftware.reflectasm.FieldAccess
import io.github.benas.randombeans.EnhancedRandomBuilder
import io.github.benas.randombeans.api.EnhancedRandom
import mu.KLogging
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import org.junit.platform.commons.support.AnnotationSupport
import java.util.stream.Stream
import kotlin.streams.toList

class RandomBeansExtension : TestInstancePostProcessor, ParameterResolver {

    companion object : KLogging() {
        private val random: EnhancedRandom by lazy {
            EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
                .objectPoolSize(10)
                .randomizationDepth(5)
                .charset(Charsets.UTF_8)
                .stringLengthRange(5, 255)
                .collectionSizeRange(1, 10)
                .scanClasspathForConcreteTypes(true)
                .overrideDefaultInitialization(false)
                .build()
        }

        private fun resolve(targetType: Class<*>, annotation: Random): Any {
            return when {
                targetType.isAssignableFrom(List::class.java) || targetType.isAssignableFrom(Collection::class.java) ->
                    random
                        .objects(annotation.type.java, annotation.size, *annotation.excludes)
                        .toList()
                targetType.isAssignableFrom(Set::class.java) ->
                    random
                        .objects(annotation.type.java, annotation.size, *annotation.excludes)
                        .toList()
                        .toSet()
                targetType.isAssignableFrom(Stream::class.java) ->
                    random.objects(annotation.type.java, annotation.size, *annotation.excludes)
                else ->
                    random.nextObject(targetType, *annotation.excludes)
            }
        }
    }


    override fun postProcessTestInstance(testInstance: Any, context: ExtensionContext) {
        // Using reflectasm library : for not private field
        val accessor = FieldAccess.get(testInstance.javaClass)

        accessor.fields.forEachIndexed { _, field ->
            if (field.type != kotlin.Lazy::class.java) {
                if (AnnotationSupport.isAnnotated(field, Random::class.java)) {
                    val randomObject = resolve(field.type, field.getAnnotation(Random::class.java))
                    accessor.set(testInstance, field.name, randomObject)
                }
            }
        }
    }

    override fun supportsParameter(parameterContext: ParameterContext,
                                   extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.getAnnotation(Random::class.java) != null
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return resolve(parameterContext.parameter.type,
                       parameterContext.parameter.getAnnotation(Random::class.java))
    }
}