package com.rostykstets.fembodydeformer.di.data

import com.rostykstets.fembodydeformer.data.sharedPreferences.SimulationRecordPreferences
import org.koin.dsl.module

val preferencesModule = module {
    single<SimulationRecordPreferences> { SimulationRecordPreferences(get()) }
}