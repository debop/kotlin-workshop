package io.github.debop.redis.examples.springdata.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Reference
import org.springframework.data.geo.Point
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.GeoIndexed
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable

enum class Gender {
    FEMALE, MALE
}

data class Address(@Indexed var city: String? = null,
                   var country: String? = null,
                   @GeoIndexed var location: Point? = null) : Serializable

@RedisHash("persons")
data class Person(
    @Indexed var firstname: String? = null,
    @Indexed var lastname: String? = null,
    var gender: Gender? = null
) : Serializable {

    @Id var id: String? = null

    var address: Address = Address()

    @Reference var children: MutableList<Person> = mutableListOf()
}