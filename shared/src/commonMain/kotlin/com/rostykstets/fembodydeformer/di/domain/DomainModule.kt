package com.rostykstets.fembodydeformer.di.domain

import com.rostykstets.fembodydeformer.data.repositories.SimulationRecordRepositoryImpl
import com.rostykstets.fembodydeformer.domain.models.FEMBody
import com.rostykstets.fembodydeformer.domain.repositories.SimulationRecordRepository
import com.rostykstets.fembodydeformer.domain.usecases.GetSimulationRecordUseCase
import com.rostykstets.fembodydeformer.domain.usecases.GetSimulationRecordsUseCase
import com.rostykstets.fembodydeformer.domain.usecases.SaveSimulationRecordUseCase
import org.koin.dsl.module

val domainModule = module {

    factory { FEMBody() }

    single<SimulationRecordRepository> { SimulationRecordRepositoryImpl(get()) }

    single { GetSimulationRecordsUseCase(get()) }

    single { SaveSimulationRecordUseCase(get()) }

    single { GetSimulationRecordUseCase(get()) }
}