package org.javers.core.model

import org.javers.core.metamodel.annotation.Id


abstract class AbstractGeneric<ID, V>(@Id var id: ID, var value: V)

class ConcreteWithActualType(id: String, value: List<String>) : AbstractGeneric<String, List<String>>(id, value)