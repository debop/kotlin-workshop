package io.github.debop.koin.example.coffeemaker

import org.koin.dsl.module

val coffeeAppModule = module {
    single { CoffeeMaker(get(), get()) }
    single<Pump> { Thermosiphon(get()) }
    single<Heater> { ElectricHeater() }
}