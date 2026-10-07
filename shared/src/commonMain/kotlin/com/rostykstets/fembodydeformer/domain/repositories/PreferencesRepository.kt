package com.rostykstets.fembodydeformer.domain.repositories

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    fun getLastOpenedProjectId(): Flow<Int>

    suspend fun saveLastOpenedProjectId(id: Int)

    suspend fun clearLastOpenedProjectId()
}