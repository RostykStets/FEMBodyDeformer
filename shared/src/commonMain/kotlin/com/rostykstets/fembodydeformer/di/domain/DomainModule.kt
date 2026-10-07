package com.rostykstets.fembodydeformer.di.domain

import com.rostykstets.fembodydeformer.data.repositories.PreferencesRepositoryImpl
import com.rostykstets.fembodydeformer.data.repositories.SimulationRecordRepositoryImpl
import com.rostykstets.fembodydeformer.domain.models.FEMBody
import com.rostykstets.fembodydeformer.domain.repositories.PreferencesRepository
import com.rostykstets.fembodydeformer.domain.repositories.SimulationRecordRepository
import com.rostykstets.fembodydeformer.domain.usecases.preferences.ClearPreferenceUseCase
import com.rostykstets.fembodydeformer.domain.usecases.preferences.GetPreferenceUseCase
import com.rostykstets.fembodydeformer.domain.usecases.preferences.SavePreferenceUseCase
import com.rostykstets.fembodydeformer.domain.usecases.records.GetSimulationRecordUseCase
import com.rostykstets.fembodydeformer.domain.usecases.records.GetSimulationRecordsUseCase
import com.rostykstets.fembodydeformer.domain.usecases.records.SaveSimulationRecordUseCase
import com.rostykstets.fembodydeformer.domain.usecases.records.UpdateSimulationRecordUseCase
import org.koin.dsl.module

val domainModule = module {

    factory { FEMBody() }

    single<SimulationRecordRepository> { SimulationRecordRepositoryImpl(get()) }

    single<PreferencesRepository> { PreferencesRepositoryImpl(get()) }

    single { GetSimulationRecordsUseCase(get()) }

    single { SaveSimulationRecordUseCase(get()) }

    single { GetSimulationRecordUseCase(get()) }

    single { UpdateSimulationRecordUseCase(get()) }

    single { GetPreferenceUseCase(get()) }

    single { SavePreferenceUseCase(get()) }

    single { ClearPreferenceUseCase(get()) }
}