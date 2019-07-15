package org.javers.core.model

/**
 * CategoryC
 *
 * @author debop
 * @since 19. 7. 15
 */
class CategoryC @JvmOverloads constructor(var id: Long,
                                          name: String = "name"): AbstractCategory("$name$id")