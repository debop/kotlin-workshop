package io.github.debop.springdata.jpa.mapping.associations.onetoone

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.persistence.PrimaryKeyJoinColumn

interface OneToOneCarRepository : JpaRepository<Car, Long>

interface OneToOneWheelRepository : JpaRepository<Wheel, Long>

/*
    Unidirectional One To One Mapping
 */

/*
    create table onetoone_car (
       id bigint generated by default as identity,
        brand varchar(255),
        primary key (id)
    )

    create table onetoone_wheel (
        diameter double,
        name varchar(255),
        car_id bigint not null,
        primary key (car_id)
    )
 */

@Entity(name = "onetoone_car")
data class Car(val brand: String) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null
}

@Entity(name = "onetoone_wheel")
data class Wheel(val name: String) {
    @Id
    var id: Long? = null

    /**
     * fetch=LAZY를 설정하면 wheel 만 가져온다
     */
    @MapsId
    @PrimaryKeyJoinColumn(name = "car_id")
    @OneToOne(cascade = [CascadeType.PERSIST], fetch = LAZY)
    var car: Car? = null

    var diameter: Double? = null
}