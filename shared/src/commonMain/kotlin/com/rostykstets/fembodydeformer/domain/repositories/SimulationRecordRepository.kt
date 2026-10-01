package com.rostykstets.fembodydeformer.domain.repositories

import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import kotlinx.coroutines.flow.Flow

interface SimulationRecordRepository {

    fun getSimulationRecordsOrderedByDateModified(): Flow<List<SimulationRecord>>

    suspend fun getSimulationRecordById(id: Int): SimulationRecord?

    suspend fun saveSimulationRecord(simulationRecord: SimulationRecord)

    suspend fun updateSimulationRecord(simulationRecord: SimulationRecord)

    suspend fun deleteSimulationRecord(simulationRecord: SimulationRecord)
}