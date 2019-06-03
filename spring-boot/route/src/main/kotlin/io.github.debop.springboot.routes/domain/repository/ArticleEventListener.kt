package io.github.debop.springboot.routes.domain.repository

import io.github.debop.springboot.routes.domain.model.Article
import io.github.debop.springboot.routes.domain.model.ArticleEvent
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent
import org.springframework.stereotype.Component

/**
 * ArticleEventListener
 * @author debop (Sunghyouk Bae)
 */
@Component
class ArticleEventListener(private val repository: ArticleEventRepository)
    : AbstractMongoEventListener<Article>() {

    override fun onAfterSave(event: AfterSaveEvent<Article>) {
        repository.save(ArticleEvent(event.source.slug, event.source.title)).subscribe()
    }
}