package io.github.debop.springboot.webmvc.web

import io.github.debop.springboot.webmvc.domain.model.Article
import io.github.debop.springboot.webmvc.domain.repository.ArticleRepository
import io.github.debop.springboot.webmvc.service.MarkdownConverter
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * ArticleController
 * @author debop (Sunghyouk Bae)
 */
@RestController
@RequestMapping("/api/article")
class ArticleController(private val articleRepo: ArticleRepository,
                        private val markdownConverter: MarkdownConverter) {

    @GetMapping("/")
    fun findAll() = articleRepo.findAllByOrderByAddedAtDesc()

    @GetMapping("/{slug}")
    fun findOne(@PathVariable slug: String, @RequestParam converter: String?) = when (converter) {
        "markdown" ->
            articleRepo.findById(slug)
                .map {
                    it.copy(headline = markdownConverter.invoke(it.headline),
                            content = markdownConverter.invoke(it.content))
                }
        null       -> articleRepo.findById(slug)
        else       -> throw IllegalArgumentException("Only markdown converter is supported")
    }

    @PostMapping("/")
    fun save(@RequestBody article: Article) = articleRepo.save(article)

    @DeleteMapping("/{slug}")
    fun delete(@PathVariable slug: String) = articleRepo.deleteById(slug)

}