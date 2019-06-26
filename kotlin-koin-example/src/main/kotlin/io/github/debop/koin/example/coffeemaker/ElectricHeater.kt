package io.github.debop.koin.example.coffeemaker

class ElectricHeater: Heater {

    private var heating: Boolean = false

    override fun on() {
        println("~ ~ ~ heating ~ ~ ~")
        heating = true
    }

    override fun off() {
        heating = false
    }

    override val isHot: Boolean
        get() = heating
}