package org.javers.spring.boot

import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeEqualTo
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.model.DepartmentEntity
import org.javers.spring.boot.model.DummyEntity
import org.javers.spring.boot.model.EmployeeEntity
import org.javers.spring.boot.repository.EmployeeRepository
import org.javers.spring.boot.repository.EmployeeRepositoryWithJavers
import org.javers.spring.boot.sql.DummyEntityRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
abstract class AbstractSpringBootAuditableIntegrationTest {

    @Autowired
    lateinit var javers: Javers

    @Autowired
    lateinit var dummyEntityRepository: DummyEntityRepository

    @Autowired
    lateinit var employeeRepository: EmployeeRepository

    @Autowired
    lateinit var employeeRepositoryWithJavers: EmployeeRepositoryWithJavers

    //    @Test
    //    fun `context loading`() {
    //        javers.shouldNotBeNull()
    //        dummyEntityRepository.shouldNotBeNull()
    //        employeeRepository.shouldNotBeNull()
    //        employeeRepositoryWithJavers.shouldNotBeNull()
    //    }

    @Test
    fun `@JaversSpringDataAuditable aspect 는 spring-boot에 손쉽게 적용할 수 있다`() {
        // GIVEN
        val entity = DummyEntity.random()
        entity.name = "a"

        val persistedEntity = dummyEntityRepository.save(entity)
        dummyEntityRepository.getOne(persistedEntity.id).name shouldBeEqualTo "a"

        persistedEntity.name = "b"
        dummyEntityRepository.saveAndFlush(persistedEntity)
        dummyEntityRepository.getOne(persistedEntity.id).name shouldBeEqualTo "b"

        // WHEN
        val snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(persistedEntity.id, DummyEntity::class.java).build())

        // THEN
        snapshots.size shouldBeEqualTo 2
        snapshots[0].getPropertyValue("name") shouldBeEqualTo "b"
        snapshots[1].getPropertyValue("name") shouldBeEqualTo "a"
    }

    @Test
    fun `@JaversSpringDataAuditable aspect는 JPA Id 자동생성을 지원합니다`() {
        // GIVEN
        val employee = createEmployee()
        employee.department!!.id.shouldBeNull()
        val freshEmployee = employeeRepository.save(employee)
        println(freshEmployee)

        // WHEN
        val jEmployee = createEmployee()
        val jFreshEmployee = employeeRepositoryWithJavers.save(jEmployee)
        println(jFreshEmployee)

        javers.findSnapshots(QueryBuilder.byInstanceId(jFreshEmployee.id, EmployeeEntity::class.java).build()).size shouldBeEqualTo 1
        javers.findSnapshots(QueryBuilder.byInstanceId(jFreshEmployee.department!!.id, DepartmentEntity::class.java).build()).size shouldBeEqualTo 1
    }

    fun createEmployee(): EmployeeEntity {
        val department = DepartmentEntity()
        return EmployeeEntity(UUID.randomUUID()).also {
            it.department = department
            department.employee.add(it)
        }
    }
}