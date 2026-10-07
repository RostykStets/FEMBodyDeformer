package com.rostykstets.fembodydeformer.data.repositories

import com.rostykstets.fembodydeformer.data.sharedPreferences.SimulationRecordPreferences
import com.rostykstets.fembodydeformer.domain.repositories.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class PreferencesRepositoryImpl(private val simulationRecordPreferences: SimulationRecordPreferences) :
    PreferencesRepository {
    override fun getLastOpenedProjectId(): Flow<Int> {
        return simulationRecordPreferences.getLastOpenedProjectId
    }

    override suspend fun saveLastOpenedProjectId(id: Int) {
        simulationRecordPreferences.saveLastOpenedProjectId(id)
    }

    override suspend fun clearLastOpenedProjectId() {
        simulationRecordPreferences.clearLastOpenedProjectId()
    }
}