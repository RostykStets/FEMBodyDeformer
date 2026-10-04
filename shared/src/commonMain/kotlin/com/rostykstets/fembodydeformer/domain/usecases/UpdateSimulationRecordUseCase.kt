package com.rostykstets.fembodydeformer.domain.usecases

import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.domain.repositories.SimulationRecordRepository

class UpdateSimulationRecordUseCase(private val simulationRecordRepository: SimulationRecordRepository) {

    suspend operator fun invoke(simulationRecord: SimulationRecord) {
        simulationRecordRepository.updateSimulationRecord(simulationRecord)
    }
}