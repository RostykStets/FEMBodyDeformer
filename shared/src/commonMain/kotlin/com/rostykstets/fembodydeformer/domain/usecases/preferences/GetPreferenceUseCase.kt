package com.rostykstets.fembodydeformer.domain.usecases.preferences

import com.rostykstets.fembodydeformer.domain.repositories.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetPreferenceUseCase(private val preferencesRepository: PreferencesRepository) {

    operator fun invoke(): Flow<Int> {
        return preferencesRepository.getLastOpenedProjectId()
    }
}