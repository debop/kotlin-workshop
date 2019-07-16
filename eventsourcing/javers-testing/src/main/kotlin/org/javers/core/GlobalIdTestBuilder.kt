package org.javers.core

import org.javers.core.metamodel.`object`.ValueObjectId

/**
 * GlobalIdTestBuilder
 *
 * @author debop
 * @since 19. 7. 16
 */
object GlobalIdTestBuilder {

    val javersTestBuilder = JaversTestBuilder.javersTestAssembly()

    fun instanceId(instance: Any) = javersTestBuilder.instanceId(instance)

    fun <T> instanceId(localId: Any, clazz: Class<T>) = javersTestBuilder.instanceId(localId, clazz)

    fun <E> valueObjectId(localId: Any, owningEntityClass: Class<E>, fragment: String) =
        ValueObjectId("?", instanceId(localId, owningEntityClass), fragment)

    fun <V> unboundedValueObjectId(valueObject: Class<V>) =
        javersTestBuilder.unboundedValueObjectId(valueObject)
}