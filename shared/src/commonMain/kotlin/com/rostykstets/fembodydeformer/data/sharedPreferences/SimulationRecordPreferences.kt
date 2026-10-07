package com.rostykstets.fembodydeformer.data.sharedPreferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SimulationRecordPreferences(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val LAST_PROJECT_ID_KEY = intPreferencesKey("last_opened_project_id")
    }

    val getLastOpenedProjectId: Flow<Int> = dataStore.data.map { preferences ->
        preferences[LAST_PROJECT_ID_KEY] ?: -1
    }

    suspend fun saveLastOpenedProjectId(id: Int) {
        dataStore.edit { preferences ->
            preferences[LAST_PROJECT_ID_KEY] = id
        }
    }

    suspend fun clearLastOpenedProjectId() {
        dataStore.edit { preferences ->
            preferences.remove(LAST_PROJECT_ID_KEY)
        }
    }
}