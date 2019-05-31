package io.github.debop.springboot.webflux.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable

/**
 * User
 * @author debop (Sunghyouk Bae)
 */
@Document
data class User(
    @Id val login: String,
    val firstname: String,
    val lastname: String,
    val description: String? = null
) : Serializable