package com.rostykstets.fembodydeformer.ui.screens.records.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.domain.usecases.records.GetSimulationRecordsUseCase
import com.rostykstets.fembodydeformer.domain.usecases.records.SaveSimulationRecordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class RecordsViewModel(
    private val getSimulationRecordsUseCase: GetSimulationRecordsUseCase,
    private val saveSimulationRecordUseCase: SaveSimulationRecordUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _records = MutableStateFlow<List<SimulationRecord>>(emptyList())
    val records = _records.asStateFlow()

    private val _isSavingRecord = MutableStateFlow(false)
    val isSavingRecord = _isSavingRecord.asStateFlow()

    private val _savedRecordId = MutableStateFlow(-1)

    init {
        loadRecords()
    }

    private fun loadRecords() {

        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            getSimulationRecordsUseCase().collect {
                _records.value = it
                _isLoading.value = false
            }
        }
    }

    fun createRecord(title: String, onCreateRecord: (id: Int) -> Unit) {

        viewModelScope.launch {
            saveRecord(title)

            onCreateRecord(_savedRecordId.value)
        }
    }

    private fun saveRecord(title: String) {

        _isSavingRecord.value = true

        val now = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())

        val newRecord = SimulationRecord(
            title = title,
            dateAdded = now,
            dateModified = now
        )

        viewModelScope.launch(Dispatchers.IO) {

            _savedRecordId.value = saveSimulationRecordUseCase(newRecord).toInt()

            _isSavingRecord.value = true
        }
    }
}