package io.github.debop.springboot.routes.web

import io.github.debop.springboot.routes.domain.repository.UserRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

/**
 * UserHandler
 *
 * @author debop
 * @since 19. 6. 3
 */
@Component
class UserHandler(private val repository: UserRepository) {

    fun findAll(req: ServerRequest) =
        ok().body(repository.findAll())

    fun findOne(req: ServerRequest) =
        ok().body(repository.findById(req.pathVariable("login")))
}