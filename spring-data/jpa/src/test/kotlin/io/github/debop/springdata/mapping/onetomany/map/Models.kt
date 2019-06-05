package io.github.debop.springdata.mapping.onetomany.map

import org.springframework.data.jpa.repository.JpaRepository
import java.io.Serializable
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.FetchType.EAGER
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.MapKeyClass
import javax.persistence.MapKeyColumn
import javax.persistence.OneToMany

interface CarRepository : JpaRepository<Car, Long>

interface CarPartRepository : JpaRepository<CarPart, Long>


@Entity(name = "onetomany_car")
data class Car(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    var id: Long? = null,
    var name: String
) : Serializable {

    @CollectionTable(name = "onetomany_car_option_map", joinColumns = [JoinColumn(name = "car_id")])
    @MapKeyClass(String::class)
    @MapKeyColumn(name = "option_key", length = 256, nullable = false)
    @ElementCollection(targetClass = CarOption::class)
    val options: MutableMap<String, CarOption> = hashMapOf()

    @OneToMany(cascade = [], fetch = LAZY)
    @JoinTable(name = "onetomany_car_part_map",
               joinColumns = [JoinColumn(name = "car_id")], inverseJoinColumns = [JoinColumn(name = "car_part_id")])
    @MapKeyColumn(name = "part_key")
    @ElementCollection(targetClass = CarPart::class, fetch = EAGER)
    val parts: MutableMap<String, CarPart> = hashMapOf()

}

@Embeddable
data class CarOption(val name: String, val value: Int = 0) : Serializable


@Entity(name = "onetomany_car_part")
data class CarPart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_part_id")
    var id: Long? = null,
    var name: String
) : Serializable {

    var description: String? = null
}