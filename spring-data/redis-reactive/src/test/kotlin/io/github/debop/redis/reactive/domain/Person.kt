package io.github.debop.redis.reactive.domain

import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.github.debop.kotlin.workshop.annotation.KotlinNoArgs
import java.io.Serializable

// no-arg constructor 를 추가로 만들기 위해 지정합니다. ( kotlin noarg plugin 참고)
@KotlinNoArgs
// @JsonTypeInfo를 지정 하면 Json 에 "_type":"io.github.debop.redis.reactive.domain.EmailAddress"
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "_type")
data class Person(
    val firstname: String = "",
    val lastname: String = "",
): Serializable