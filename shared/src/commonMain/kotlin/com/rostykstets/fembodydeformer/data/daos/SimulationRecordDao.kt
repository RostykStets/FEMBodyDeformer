package com.rostykstets.fembodydeformer.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.rostykstets.fembodydeformer.data.entities.SimulationRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SimulationRecordDao {

    @Query("SELECT * FROM SimulationRecords ORDER BY date_modified DESC")
    fun getSimulationRecordsOrderedByDateModified(): Flow<List<SimulationRecordEntity>>

    @Insert
    suspend fun saveSimulationRecord(simulationRecordEntity: SimulationRecordEntity)

    @Update
    suspend fun updateSimulationRecord(simulationRecordEntity: SimulationRecordEntity)

    @Delete
    suspend fun deleteSimulationRecord(simulationRecordEntity: SimulationRecordEntity)
}