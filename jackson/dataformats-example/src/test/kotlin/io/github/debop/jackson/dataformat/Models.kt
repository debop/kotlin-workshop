package io.github.debop.jackson.dataformat

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.github.debop.kotlin.workshop.annotation.KotlinValueClass
import org.testcontainers.shaded.org.bouncycastle.util.Arrays
import java.util.Objects

@KotlinValueClass
data class Box(val x: Int, val y: Int)

@KotlinValueClass
data class Container(val boxes: List<Box>)

@KotlinValueClass
@JsonPropertyOrder(value = ["x", "y"])
data class Point(val x: Int, val y: Int)

@KotlinValueClass
data class Points(val p: List<Point>) {
    constructor(vararg points: Point): this(points.toList())
}

@KotlinValueClass
@JsonPropertyOrder(value = ["topLeft", "bottomRight"])
data class Rectangle(val topLeft: Point, val bottomRight: Point)

enum class Gender {
    MALE, FEMALE;
}

@KotlinValueClass
data class FiveMinuteUser(val firstName: String,
                          val lastName: String,
                          var verified: Boolean,
                          var gender: Gender,
                          var userImage: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if(other === this) {
            return true
        }
        if(other == null || other !is FiveMinuteUser) {
            return false
        }

        if(verified != other.verified) return false
        if(gender != other.gender) return false
        if(firstName != other.firstName) return false
        if(lastName != other.lastName) return false
        if(!Arrays.areEqual(userImage, other.userImage)) return false

        return true
    }

    override fun hashCode(): Int = Objects.hashCode(firstName)
}

@KotlinValueClass
@JsonPropertyOrder(value = ["id", "desc"])
data class IdDesc(var id: String, val desc: String)

@KotlinValueClass
data class Outer(val name: Name, val age: Int)

@KotlinValueClass
data class Name(val first: String, val last: String)


@KotlinValueClass
data class Database(val dataSource: DataSource)

@KotlinValueClass
data class DataSource(val driverClass: String, val url: String, val username: String, val password: String, val properties: Set<String>)

