package com.rostykstets.fembodydeformer.domain.usecases

import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.domain.repositories.SimulationRecordRepository

class GetSimulationRecordUseCase(private val simulationRecordRepository: SimulationRecordRepository) {

    suspend operator fun invoke(id: Int): SimulationRecord? {
        return simulationRecordRepository.getSimulationRecordById(id)
    }
}