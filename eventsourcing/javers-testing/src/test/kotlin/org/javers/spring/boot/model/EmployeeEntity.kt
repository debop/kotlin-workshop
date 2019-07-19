package org.javers.spring.boot.model

import java.io.Serializable
import java.util.UUID
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "employee")
data class EmployeeEntity(
    @Id
    var id: UUID = UUID.randomUUID()
): Serializable {

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    var department: DepartmentEntity? = null
}