package io.github.debop.springboot.routes.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

@Configuration
class Router(
    private val userHandler: UserHandler,
    private val articleHandler: ArticleHandler,
) {

    @Bean
    fun appRouter() = router {
        accept(MediaType.APPLICATION_JSON).nest {
            "/api/user".nest {
                GET("/", userHandler::findAll)
                GET("/{login}", userHandler::findOne)
            }
            "/api/article".nest {
                GET("/", articleHandler::findAll)
                GET("/{slug}", articleHandler::findOne)
                POST("/", articleHandler::save)
                DELETE("/{slug}", articleHandler::delete)
            }

            // (GET("/api/article/notifications") and accept(MediaType.TEXT_EVENT_STREAM)).invoke(articleHandler::notifications)
            accept(MediaType.TEXT_EVENT_STREAM).nest {
                "/api/article/notifications".nest {
                    GET("/", articleHandler::notifications)
                }
            }
        }
    }
}