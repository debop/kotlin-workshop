package io.github.kotlin.dagger.example

import org.junit.jupiter.api.RepeatedTest
import kotlin.system.measureTimeMillis

/**
 * DaggerCoffeeShopTests
 *
 * @author debop
 * @since 19. 6. 4
 */
class DaggerCoffeeShopTests {

    @RepeatedTest(3)
    fun `coffeeShop singleton by dagger`() {
        val elapsedTime = measureTimeMillis {
            val coffeeShop = DaggerCoffeeShop.builder().build()
            coffeeShop.maker().brew()
        }
        println("executed in $elapsedTime ms")
    }
}