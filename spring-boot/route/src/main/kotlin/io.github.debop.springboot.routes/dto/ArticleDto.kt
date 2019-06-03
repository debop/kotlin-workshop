package io.github.debop.springboot.routes.dto

import io.github.debop.springboot.routes.domain.model.Article
import io.github.debop.springboot.routes.domain.model.User
import io.github.debop.springboot.routes.domain.repository.UserRepository
import io.github.debop.springboot.routes.formatDate
import io.github.debop.springboot.routes.service.MarkdownConverter
import reactor.core.publisher.Mono

data class ArticleDto(
    val slug: String,
    val title: String,
    val headline: String,
    val content: String,
    val author: User,
    val addedAt: String
)

fun Article.toDto(userRepository: UserRepository,
                  markdownConverter: MarkdownConverter): Mono<ArticleDto> =
    userRepository
        .findById(author)
        .map {
            ArticleDto(
                slug,
                title,
                markdownConverter.invoke(headline),
                markdownConverter.invoke(content),
                it,
                addedAt.formatDate()
            )
        }