package io.github.debop.springboot.webmvc.domain.repository

import io.github.debop.springboot.webmvc.domain.model.Article
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * ArticleRepository
 * @author debop (Sunghyouk Bae)
 */
@Repository
interface ArticleRepository : CrudRepository<Article, String> {

    fun findAllByOrderByAddedAtDesc(): Iterable<Article>
}