package io.github.debop.springboot.routes.web

import io.github.debop.springboot.routes.domain.model.Article
import io.github.debop.springboot.routes.domain.model.ArticleEvent
import io.github.debop.springboot.routes.domain.repository.ArticleEventRepository
import io.github.debop.springboot.routes.domain.repository.ArticleRepository
import io.github.debop.springboot.routes.service.MarkdownConverter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.sse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * ArticleHandler
 *
 * @author debop
 * @since 19. 6. 3
 */
@Component
class ArticleHandler(
    private val articleRepository: ArticleRepository,
    private val articleEventRepository: ArticleEventRepository,
    private val markdownConverter: MarkdownConverter,
) {

    private val notifications: Flux<ArticleEvent> = articleEventRepository
        .count()
        .flatMapMany { articleEventRepository.findWithTailableCursorBy().skip(it) }
        .share()

    fun findAll(req: ServerRequest) =
        ok().body(articleRepository.findAllByOrderByAddedAtDesc())

    fun findOne(req: ServerRequest): Mono<ServerResponse> {
        val converter = req.queryParam("converter").orElse(null)
        val slug = req.pathVariable("slug")

        val article = when (converter) {
            "markdown" ->
                articleRepository.findById(slug)
                    .map {
                        it.copy(headline = markdownConverter.invoke(it.headline),
                                content = markdownConverter.invoke(it.content))
                    }
            null       -> articleRepository.findById(slug)
            else       -> throw IllegalArgumentException("Only markdown converter is supported")
        }

        return ok().body(article)
    }

    fun save(req: ServerRequest) =
        ok().body(articleRepository.saveAll(req.bodyToMono<Article>()))

    fun delete(req: ServerRequest) =
        ok().body(articleRepository.deleteById(req.pathVariable("slug")))

    fun notifications(req: ServerRequest) =
        ok().sse().body(notifications)
    //ok().bodyToServerSentEvents(notifications)

}