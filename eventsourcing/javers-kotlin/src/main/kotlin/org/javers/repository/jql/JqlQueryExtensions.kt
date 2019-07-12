package org.javers.repository.jql

import org.javers.core.Changes
import org.javers.core.Javers
import org.javers.core.metamodel.`object`.CdoSnapshot
import org.javers.shadow.Shadow
import java.util.stream.Stream
import kotlin.streams.asSequence

inline fun <reified T> JqlQuery.findShadows(javers: Javers): MutableList<Shadow<T>> =
    javers.findShadows<T>(this)

inline fun <reified T> JqlQuery.findShadowsAndStream(javers: Javers): Stream<Shadow<T>> =
    javers.findShadowsAndStream<T>(this)

inline fun <reified T> JqlQuery.findShadowsAndSequence(javers: Javers): Sequence<Shadow<T>> =
    javers.findShadowsAndStream<T>(this).asSequence()

fun JqlQuery.findSnapshots(javers: Javers): MutableList<CdoSnapshot> =
    javers.findSnapshots(this)

fun JqlQuery.findChanges(javers: Javers): Changes =
    javers.findChanges(this)

