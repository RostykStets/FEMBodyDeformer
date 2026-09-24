package com.rostykstets.fembodydeformer.di.data

import com.rostykstets.fembodydeformer.data.daos.SimulationRecordDao
import com.rostykstets.fembodydeformer.data.database.FEMBodyDeformerDatabase
import org.koin.dsl.module

val daoModule = module {
    single<SimulationRecordDao> { get<FEMBodyDeformerDatabase>().getSimulationRecordDao() }
}