package com.rostykstets.fembodydeformer.data.repositories

import com.rostykstets.fembodydeformer.data.daos.SimulationRecordDao
import com.rostykstets.fembodydeformer.data.mappers.toEntity
import com.rostykstets.fembodydeformer.data.mappers.toModel
import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.domain.repositories.SimulationRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SimulationRecordRepositoryImpl(private val simulationRecordDao: SimulationRecordDao) :
    SimulationRecordRepository {

    override fun getSimulationRecordsOrderedByDateModified(): Flow<List<SimulationRecord>> {

        val simulationRecords = simulationRecordDao.getSimulationRecordsOrderedByDateModified()

        return simulationRecords.map { it.map { entity -> entity.toModel() } }

    }

    override suspend fun getSimulationRecordById(id: Int): SimulationRecord? {
        return simulationRecordDao.getSimulationRecordById(id)?.toModel()
    }

    override suspend fun saveSimulationRecord(simulationRecord: SimulationRecord): Long {
        return simulationRecordDao.saveSimulationRecord(simulationRecord.toEntity())
    }

    override suspend fun updateSimulationRecord(simulationRecord: SimulationRecord) {
        simulationRecordDao.updateSimulationRecord(simulationRecord.toEntity())
    }

    override suspend fun deleteSimulationRecord(simulationRecord: SimulationRecord) {
        simulationRecordDao.deleteSimulationRecord(simulationRecord.toEntity())
    }
}