package io.github.debop.springdata.jpa.mapping.compositeid

import java.io.Serializable
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

// see build script
// kotlin("plugin.noarg") help to define no-arg constructor
//
data class CarIdentifier(val brand: String? = null, val year: Int? = null) : Serializable

/*
    create table idclass_car (
       brand varchar(32) not null,
        year integer not null,
        serial_no varchar(255),
        primary key (brand, year)
    )
 */

@Entity(name = "idclass_car")
@IdClass(CarIdentifier::class)
@Access(AccessType.FIELD)
data class IdClassCar(
        @Id
        @Column(nullable = false, length = 32)
        val brand: String? = null,
        @Id
        @Column(nullable = false)
        val year: Int? = null
) : Serializable {
    var serialNo: String? = null
}

/*
    create table embeddedid_car (
       brand varchar(32) not null,
        year integer not null,
        serial_no varchar(255),
        primary key (brand, year)
    )
 */

@Embeddable
@Access(AccessType.FIELD)
data class EmbeddableCarId(
        @Column(nullable = false, length = 32)
        val brand: String,
        @Column(nullable = false)
        val year: Int
) : Serializable

@Entity(name = "embeddedid_car")
@Access(AccessType.FIELD)
data class EmbeddedIdCar(@EmbeddedId val id: EmbeddableCarId) : Serializable {
    var serialNo: String? = null
}

