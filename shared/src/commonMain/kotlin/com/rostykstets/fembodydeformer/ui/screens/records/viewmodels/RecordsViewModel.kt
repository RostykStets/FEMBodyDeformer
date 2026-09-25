package com.rostykstets.fembodydeformer.ui.screens.records.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.domain.usecases.GetSimulationRecordsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecordsViewModel(private val getSimulationRecordsUseCase: GetSimulationRecordsUseCase) :
    ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _records = MutableStateFlow<List<SimulationRecord>>(emptyList())
    val records = _records.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {

        _isLoading.value = true

        viewModelScope.launch {
            getSimulationRecordsUseCase().collect {
                _records.value = it
            }
        }
    }
}