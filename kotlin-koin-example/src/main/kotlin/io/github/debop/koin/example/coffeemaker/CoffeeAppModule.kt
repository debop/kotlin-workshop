package io.github.debop.koin.example.coffeemaker

import org.koin.dsl.module

val coffeeAppModule = module {
    single<CoffeeMaker> { CoffeeMaker(get(), get()) }
    single<Pump>(createdAtStart = false, override = true) { Thermosiphon(get()) }
    single<Heater>(createdAtStart = false, override = false) { ElectricHeater() }
}