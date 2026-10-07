package com.rostykstets.fembodydeformer.domain.usecases.records

import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.domain.repositories.SimulationRecordRepository
import kotlinx.coroutines.flow.Flow

class GetSimulationRecordsUseCase(private val simulationRecordRepository: SimulationRecordRepository) {

    operator fun invoke(): Flow<List<SimulationRecord>> {
        return simulationRecordRepository.getSimulationRecordsOrderedByDateModified()
    }
}