package io.github.debop.javers.examples

import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import java.time.ZonedDateTime

enum class Position {
    Assistant,
    Secretary,
    Developer,
    Specialist,
    Saleswoman,
    ScrumMaster,
    Townsman,
    Hero
}

data class Address(var city: String, var street: String = "")

@TypeName("Person")
data class Person(@Id val login: String, var name: String) {
    val addresses: MutableList<Address> = mutableListOf()
    val addressMap: MutableMap<String, Address> = mutableMapOf()
    var position: Position? = null
}

@TypeName("Employee")
data class Employee(@Id val name: String, var salary: Int = 1000, val position: Position = Position.Developer) {

    var age: Int? = null

    var boss: Employee? = null
    val subordinates: MutableList<Employee> = mutableListOf()

    var primaryAddress: Address? = null
    var postalAddress: Address? = null
    val skills: MutableSet<String> = mutableSetOf()
    val performance: MutableMap<Int, String> = mutableMapOf()
    var lastPromotionDate: ZonedDateTime? = null

    fun addSubordinates(vararg emps: Employee) {
        subordinates.addAll(emps)
        emps.forEach { it.boss = this }
    }
}


@TypeName("Boss")
data class Boss(@Id val name: String) {
    val suordinates: MutableCollection<Person> = mutableListOf()
}