package io.github.debop.springboot.webflux.service

import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.stereotype.Service

/**
 * MarkdownConverter
 * @author debop (Sunghyouk Bae)
 */
@Service
class MarkdownConverter : (String?) -> String {

    private val parser = Parser.builder()
        .extensions(listOf(AutolinkExtension.create()))
        .build()

    private val renderer = HtmlRenderer.builder().build()

    override fun invoke(input: String?): String {
        if (input.isNullOrEmpty()) {
            return ""
        }
        return renderer.render(parser.parse(input))
    }
}