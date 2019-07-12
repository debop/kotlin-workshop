package org.javers.repository.jql

import kotlin.reflect.KClass

/**
 * QueryBuilderExtensions
 *
 * @author debop
 * @since 19. 7. 12
 */

fun <T: Any> T.queryBuilder(): QueryBuilder = QueryBuilder.byInstance(this)

inline fun <reified T: Any> KClass<T>.queryBuilder(): QueryBuilder = QueryBuilder.byClass(T::class.java)

inline fun <reified T> QueryBuilder.byKClass(): QueryBuilder =
    QueryBuilder.byClass(T::class.java)

fun QueryBuilder.byKClass(vararg requiredKClasses: KClass<*>): QueryBuilder =
    QueryBuilder.byClass(*requiredKClasses.map { it.java }.toTypedArray())

inline fun <reified T> QueryBuilder.byInstanceId(localId: Any): QueryBuilder =
    QueryBuilder.byInstanceId(localId, T::class.java)

inline fun <reified T> QueryBuilder.byValueObject(path: String): QueryBuilder =
    QueryBuilder.byValueObject(T::class.java, path)

inline fun <reified T> QueryBuilder.byValueObjectId(ownerLocalId: Any, path: String): QueryBuilder =
    QueryBuilder.byValueObjectId(ownerLocalId, T::class.java, path)



