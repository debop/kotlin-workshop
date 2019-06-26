package io.github.debop.koin.example.coffeemaker

interface Heater {
    fun on()
    fun off()
    val isHot: Boolean
}