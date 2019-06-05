package io.github.debop.springdata.jpa.entities

import java.io.Serializable

/**
 * JpaEntity
 *
 * @author debop
 * @since 19. 6. 5
 */
interface JpaEntity<TId : Serializable> {

    var id: TId?

    @JvmDefault
    fun resetIdentifier() {
        id = null
    }
}