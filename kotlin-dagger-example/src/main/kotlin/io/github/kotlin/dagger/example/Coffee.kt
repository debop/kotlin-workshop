package io.github.kotlin.dagger.example

import dagger.Binds
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

interface Heater {
    fun on()
    fun off()
    val isHot: Boolean
}

interface Pump {
    fun pump()
}

class ElectricHeader : Heater {
    var heating: Boolean = false

    override fun on() {
        println("~ ~ ~ heating ~ ~ ~")
        this.heating = true
    }

    override fun off() {
        this.heating = false
    }

    override val isHot: Boolean get() = heating
}

class Thermosiphon @Inject constructor(private val heater: Heater) : Pump {

    override fun pump() {
        if (heater.isHot) {
            println("=> => pumping => =>")
        }
    }
}

@Module
abstract class PumpModule {
    // Binds 는 지정된 component를 제공하도록 합니다. (see: `@Provides`)
    @Binds
    abstract fun providePump(pump: Thermosiphon): Pump
}

@Module(includes = [PumpModule::class])
class DripCoffeeModule {

    @Provides
    @Singleton
    fun provideHeater(): Heater = ElectricHeader()
}

class CoffeeMaker @Inject constructor(private val heater: dagger.Lazy<Heater>,
                                      private val pump: Pump) {
    fun brew() {
        heater.get().on()
        pump.pump()
        println("[_]P coffee! [_]P")
        heater.get().off()
    }
}

@Singleton
@Component(modules = [DripCoffeeModule::class])
interface CoffeeShop {
    fun maker(): CoffeeMaker
}


