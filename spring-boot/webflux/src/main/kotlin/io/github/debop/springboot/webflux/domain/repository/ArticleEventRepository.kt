package io.github.debop.springboot.webflux.domain.repository

import io.github.debop.springboot.webflux.domain.model.ArticleEvent
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

/**
 * ArticleEventRepository
 * @author debop (Sunghyouk Bae)
 */
@Repository
interface ArticleEventRepository : ReactiveCrudRepository<ArticleEvent, String> {

    @Tailable
    fun findWithTailableCursorBy(): Flux<ArticleEvent>
}