package io.github.kotlin.dagger.example

import org.junit.jupiter.api.Test

/**
 * DaggerCoffeeShopTests
 *
 * @author debop
 * @since 19. 6. 4
 */
class DaggerCoffeeShopTests {

    @Test
    fun `coffeeShop singleton by dagger`() {
        val coffeeShop = DaggerCoffeeShop.builder().build()
        coffeeShop.maker().brew()
    }
}