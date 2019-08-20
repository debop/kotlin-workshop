package io.github.debop.koin.example.coffeemaker

import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

val coffeeAppModule = module {
    single { CoffeeMaker(get(), get()) }
    //    single<Pump>(createdAtStart = false, override = true) { Thermosiphon(get()) }
    //    single<Heater>(createdAtStart = false, override = false) { ElectricHeater() }

    // koin-core-ext 의 extension method 를 사용할 수 있다 
    singleBy<Pump, Thermosiphon>(createOnStart = false, override = true)
    singleBy<Heater, ElectricHeater>(createOnStart = false, override = false)
}