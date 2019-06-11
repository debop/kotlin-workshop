package io.github.debop.springdata.jpa.mapping.inheritance.joined

import org.springframework.data.jpa.repository.JpaRepository

interface JoinedEmployeeRepository: JpaRepository<Employee, Long>

interface JoinedCustomerRepository: JpaRepository<Customer, Long>