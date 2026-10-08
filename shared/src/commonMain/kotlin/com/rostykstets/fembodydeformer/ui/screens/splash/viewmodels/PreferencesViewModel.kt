package com.rostykstets.fembodydeformer.ui.screens.splash.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rostykstets.fembodydeformer.domain.usecases.preferences.GetPreferenceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreferencesViewModel(private val getPreferenceUseCase: GetPreferenceUseCase) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _lastOpenProjectId = MutableStateFlow(-1)
    val lastOpenProjectId = _lastOpenProjectId.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {

        _isLoading.value = true

        viewModelScope.launch {
            getPreferenceUseCase().collect {
                _lastOpenProjectId.value = it

                _isLoading.value = false
            }
        }
    }
}