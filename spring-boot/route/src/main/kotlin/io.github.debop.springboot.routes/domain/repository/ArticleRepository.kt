package io.github.debop.springboot.routes.domain.repository

import io.github.debop.springboot.routes.domain.model.Article
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
 * ArticleRepository
 * @author debop (Sunghyouk Bae)
 */
@Repository
interface ArticleRepository : ReactiveCrudRepository<Article, String> {

    fun findAllByOrderByAddedAtDesc(): Flux<Article>
}