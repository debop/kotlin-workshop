package org.javers.core.model

import com.google.common.collect.Multimap
import com.google.common.collect.Multiset
import org.javers.core.metamodel.annotation.Id
import java.math.BigDecimal
import java.time.LocalDate
import java.util.EnumSet
import java.util.Optional

/**
 * SnapshotEntity
 *
 * @author debop
 * @since 19. 7. 15
 */
data class SnapshotEntity(@Id var id: Int,
                          var entityRef: SnapshotEntity? = null) {


    enum class DummyEnum { val1, val2, val3 }

    var dob: LocalDate? = null

    var intProperty: Int? = null

    var arrayOfIntegers: IntArray? = null
    var arrayOfDates: Array<LocalDate>? = null
    var arrayOfEntities: Array<SnapshotEntity>? = null
    var arrayOfValueObjects: Array<DummyAddress>? = null

    val listOfIntegers: List<Int> = emptyList()
    val listOfDates: List<LocalDate> = emptyList()
    val listOfValueObjects: List<DummyAddress> = emptyList()
    val polymorficList: List<Any?> = emptyList()

    val setOfIntegers: Set<Int> = emptySet()
    val setOfDates: Set<LocalDate> = emptySet()
    val setOfValueObjects: Set<DummyAddress> = emptySet()
    val polymorficset: Set<Any?> = emptySet()

    var optionalInt: Optional<Int> = Optional.empty()
    var optionalDate: Optional<LocalDate> = Optional.empty()
    var optionalEntity: Optional<SnapshotEntity> = Optional.empty()
    var optionalValueObject: Optional<DummyAddress> = Optional.empty()

    var multiSetOfPrimitives: Multiset<String>? = null
    var multiSetOfValueObject: Multiset<DummyAddress>? = null
    var multiSetOfEntities: Multiset<SnapshotEntity>? = null

    var multiMapOfPrimitives: Multimap<String, String>? = null
    var multiMapPrimitiveToValueObject: Multimap<String, DummyAddress>? = null
    var multiMapPrimitiveToEntity: Multimap<String, SnapshotEntity>? = null
    var multiMapEntityToEntity: Multimap<SnapshotEntity, SnapshotEntity>? = null
    var multiMapValueObjectToValueObject: Multimap<DummyAddress, DummyAddress>? = null // not supported

    val mapOfPrimitives: MutableMap<String, Int> = mutableMapOf()
    val mapOfValues: MutableMap<LocalDate, BigDecimal> = mutableMapOf()
    val mapPrimitiveToVO: MutableMap<String, DummyAddress> = mutableMapOf()
    val mapPrimitiveToEntity: MutableMap<String, SnapshotEntity> = mutableMapOf()
    val polymorficMap: MutableMap<Any, Any?> = mutableMapOf()
    val mapOfGenericValues: MutableMap<String, EnumSet<DummyEnum>> = mutableMapOf()

    var shallowPhone: ShallowPhone? = null
    val shallowPhones: MutableSet<ShallowPhone> = mutableSetOf()
    val shallowPhoneList: MutableList<ShallowPhone> = mutableListOf()
    val shallowPhonesMap: MutableMap<String, ShallowPhone> = mutableMapOf()

    val mapVoToPrimitive: MutableMap<DummyAddress, String> = mutableMapOf()  // not supported
    var nonParameterizedMap: Map<*, *>? = null                                       // not supported

}