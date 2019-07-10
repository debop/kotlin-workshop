package io.github.debop.koin.example.coffeemaker

class Thermosiphon(private val heater: Heater): Pump {
    override fun pump() {
        if (heater.isHot) {
            println("=> => pumping => =>")
        }
    }
}