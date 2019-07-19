package org.javers.spring.boot.model

import java.io.Serializable
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "department")
data class DepartmentEntity(
    @Id
    @GeneratedValue
    @org.javers.core.metamodel.annotation.Id
    var id: UUID? = null
): Serializable {

    @OneToMany(mappedBy = "department")
    var employee: MutableList<EmployeeEntity> = mutableListOf()
}