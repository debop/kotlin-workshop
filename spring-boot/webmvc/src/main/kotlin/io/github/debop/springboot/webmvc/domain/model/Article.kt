package io.github.debop.springboot.webmvc.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.time.LocalDateTime

@Document
data class Article(
    @Id val slug: String,
    val title: String,
    val headline: String,
    val content: String,
    @DBRef val author: User,
    val addedAt: LocalDateTime = LocalDateTime.now()
) : Serializable

@Document
data class ArticleEvent(@Id val slug: String, val title: String) : Serializable