package org.javers

import org.javers.core.Javers
import org.javers.core.metamodel.`object`.InstanceId
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.ValueObjectType


inline fun <reified T> Javers.getEntityTypeMapping(): EntityType =
    this.getTypeMapping<EntityType>(T::class.java)

inline fun <reified T> Javers.getValueObjectTypeMapping(): ValueObjectType =
    this.getTypeMapping<ValueObjectType>(T::class.java)


inline fun <reified T> Javers.createEntityInstanceId(entity: T): InstanceId =
    getEntityTypeMapping<T>().createIdFromInstance(entity)
