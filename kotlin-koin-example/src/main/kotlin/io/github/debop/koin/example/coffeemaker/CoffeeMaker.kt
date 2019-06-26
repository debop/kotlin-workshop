package io.github.debop.koin.example.coffeemaker

/**
 * CoffeeMaker
 *
 * @author debop (Sunghyouk Bae)
 * @since 19. 6. 26
 */
class CoffeeMaker(private val pump: Pump,
                  private val heater: Heater) {

    fun brew() {
        heater.on()
        while(!heater.isHot) {
            println("Wait ...")
            Thread.sleep(1000)
        }
        pump.pump()
        println(" [_]P coffee! [_]P ")
        heater.off()
    }
}