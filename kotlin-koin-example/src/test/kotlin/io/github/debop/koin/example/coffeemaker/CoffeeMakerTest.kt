package io.github.debop.koin.example.coffeemaker

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

class CoffeeMakerTest: AutoCloseKoinTest() {

    private val coffeeMaker: CoffeeMaker by lazy { getKoin().get<CoffeeMaker>() }  //inject()
    private val heater: Heater by inject()

    @BeforeEach
    fun setup() {
        startKoin {
            printLogger(Level.DEBUG)
            modules(coffeeAppModule)
        }

        declareMock<Heater> {
            given(isHot).will { true }
        }
    }

    @Test
    fun `heater is turned on and then off`() {
        coffeeMaker.brew()

        verify(heater, times(1)).on()
        verify(heater, times(1)).off()
    }
}