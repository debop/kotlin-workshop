package io.github.debop.springboot.webmvc.dto

import io.github.debop.springboot.webmvc.domain.model.Article
import io.github.debop.springboot.webmvc.domain.model.User
import io.github.debop.springboot.webmvc.formatDate
import io.github.debop.springboot.webmvc.service.MarkdownConverter

data class ArticleDto(
    val slug: String,
    val title: String,
    val headline: String,
    val content: String,
    val author: User,
    val addedAt: String
)

fun Article.toDto(markdownConverter: MarkdownConverter): ArticleDto =
    ArticleDto(
        slug,
        title,
        markdownConverter.invoke(headline),
        markdownConverter.invoke(content),
        author,
        addedAt.formatDate()
    )
