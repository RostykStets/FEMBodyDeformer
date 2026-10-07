package com.rostykstets.fembodydeformer.domain.usecases.preferences

import com.rostykstets.fembodydeformer.domain.repositories.PreferencesRepository

class ClearPreferenceUseCase(private val preferencesRepository: PreferencesRepository) {

    suspend operator fun invoke() {
        preferencesRepository.clearLastOpenedProjectId()
    }
}