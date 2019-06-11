package io.github.debop.springdata.jpa.mapping.compositeid

import java.io.Serializable
import javax.persistence.*

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
data class IdClassCar(
        @Id
        @Column(nullable = false, length = 32)
        var brand: String? = null,
        @Id
        @Column(nullable = false)
        var year: Int? = null
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
data class EmbeddableCarId(
        @Column(nullable = false, length = 32)
        val brand: String,
        @Column(nullable = false)
        val year: Int
) : Serializable

@Entity(name = "embeddedid_car")
data class EmbeddedIdCar(@EmbeddedId val id: EmbeddableCarId) : Serializable {
    var serialNo: String? = null
}

