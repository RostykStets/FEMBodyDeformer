package com.rostykstets.fembodydeformer.domain.usecases.preferences

import com.rostykstets.fembodydeformer.domain.repositories.PreferencesRepository

class SavePreferenceUseCase(private val preferencesRepository: PreferencesRepository) {

    suspend operator fun invoke(id: Int) {
        preferencesRepository.saveLastOpenedProjectId(id)
    }
}