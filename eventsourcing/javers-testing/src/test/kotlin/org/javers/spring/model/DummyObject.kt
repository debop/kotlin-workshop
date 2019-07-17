package org.javers.spring.model

import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class DummyObject(@Id var id: String = UUID.randomUUID().toString(),
                       var name: String? = null)