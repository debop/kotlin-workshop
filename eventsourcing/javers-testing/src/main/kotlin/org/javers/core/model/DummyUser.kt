package org.javers.core.model

import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.Id
import java.time.LocalDateTime

/**
 * DummyUser
 *
 * @author debop
 * @since 19. 7. 16
 */
class DummyUser(@Id val name: String, var surname: String? = null) : AbstractDummyUser() {

    enum class SEX { FEMALE, MALE, OCCASIONALLY }

    companion object {
        fun dummyUser(name: String = "name") = DummyUser(name)
    }

    @Transient var someTransientField: Int = 0

    @javax.persistence.Transient
    var propertyWithTransientAnn: Int = 0

    @DiffIgnore
    var propertyWithDiffIgnoreAnn: Int = 0

    var propertyWithDiffIgnoredType: DummyIgnoredType? = null
    var propertyWithDiffIgnoredSubType: IgnoredSubType? = null

    // primitives and primitive boxes
    var flag: Boolean = false
    var boxedFlag: Boolean? = null
    var age: Int = 0
    var _char: Char = 0.toChar()

    var sex: SEX? = null
    var largeInt: Int? = null

    // collections
    var stringSet: MutableSet<String> = mutableSetOf()
    var stringList: MutableList<String> = mutableListOf()
    var integerList: MutableList<Int> = mutableListOf()
    var primitiveMap: MutableMap<String, LocalDateTime> = mutableMapOf()
    var valueMap: MutableMap<String, LocalDateTime> = mutableMapOf()

    var objectMap: MutableMap<String, DummyUserDetails> = mutableMapOf()   // not supported

    // array
    var intArray: IntArray? = null
    var dateTimes: Array<LocalDateTime>? = null

    // reference
    var supervisor: DummyUser? = null
    var dummyUserDetails: DummyUserDetails? = null
    var dummyUserDetailsList: MutableList<DummyUserDetails> = mutableListOf()
    var employeesList: MutableList<DummyUser> = mutableListOf()

    fun addEmployee(employee: DummyUser) {
        employeesList.add(employee)
        employee.supervisor = this
    }

    fun withDetails(id: Int = 1) = apply {
        dummyUserDetails = DummyUserDetails(id)
    }

    fun withAddress(city: String) = apply {
        if (dummyUserDetails == null) {
            withDetails()
        }
        dummyUserDetails!!.dummyAddress = DummyAddress(city = city)
    }

    fun withAddresses(vararg addresses: DummyAddress) = apply {
        if (dummyUserDetails == null) {
            withDetails()
        }
        dummyUserDetails!!.addressList.addAll(addresses)
    }

    fun withSex(sex: SEX) = apply { this.sex = sex }

    fun withPrimitiveMap(map: MutableMap<String, LocalDateTime>) = apply {
        this.primitiveMap = map
    }

    fun withValueMap(map: MutableMap<String, LocalDateTime>) = apply {
        this.valueMap = map
    }

    fun withStringsSet(strings: MutableSet<String>) = apply {
        this.stringSet = strings
    }

    fun withIntegerList(list: MutableList<Int>) = apply {
        this.integerList = list
    }

    fun withAge(age: Int) = apply { this.age = age }

    fun withBoxedFlag(boxedFlag: Boolean) = apply { this.boxedFlag = boxedFlag }

    fun withInteger(largeInt: Int) = apply { this.largeInt = largeInt }

    fun withFlag(flag: Boolean) = apply { this.flag = flag }

    fun withSupervisor(supervisorName: String) = apply {
        this.supervisor = DummyUser(name = supervisorName)
    }

    fun withSupervisor(supervisor: DummyUser) = apply {
        this.supervisor = supervisor
    }

    fun withEmployees(numberOfEmployees: Int) = apply {
        repeat(numberOfEmployees) {
            this.addEmployee(DummyUser(name = "EM${it + 1}"))
        }
    }

    fun withEmployees(employees: List<DummyUser>) = apply {
        employees.forEach { this.addEmployee(it) }
    }

    fun withDetailsList(numberOfDetailsList: Int) = apply {
        this.dummyUserDetailsList = List(numberOfDetailsList) { DummyUserDetails(id = it) }.toMutableList()
    }

    fun withIntArray(ints: List<Int>) = apply {
        this.intArray = ints.toIntArray()
    }
}