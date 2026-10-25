package com.rostykstets.fembodydeformer.ui.screens.scene.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rostykstets.fembodydeformer.domain.models.ElementFace
import com.rostykstets.fembodydeformer.domain.models.FEMBody
import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.domain.usecases.preferences.SavePreferenceUseCase
import com.rostykstets.fembodydeformer.domain.usecases.records.GetSimulationRecordUseCase
import com.rostykstets.fembodydeformer.domain.usecases.records.UpdateSimulationRecordUseCase
import com.rostykstets.fembodydeformer.ui.models.Vector3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class FEMViewModel(
    val femBody: FEMBody,
    private val getSimulationRecordUseCase: GetSimulationRecordUseCase,
    private val updateSimulationRecordUseCase: UpdateSimulationRecordUseCase,
    private val savePreferenceUseCase: SavePreferenceUseCase
) : ViewModel() {

    private val _bodySize = MutableStateFlow(Vector3(4f, 4f, 4f))
    val bodySize = _bodySize.asStateFlow()

    private val _bodySplits = MutableStateFlow(Vector3(2f, 1f, 2f))
    val bodySplits = _bodySplits.asStateFlow()

    private val _youngModule = MutableStateFlow("4")
    val youngModule = _youngModule.asStateFlow()

    private val _poissonRatio = MutableStateFlow("0.3")
    val poissonRatio = _poissonRatio.asStateFlow()

    private val _pressure = MutableStateFlow("2")
    val pressure = _pressure.asStateFlow()

    private val _deformedBody = MutableStateFlow<DoubleArray?>(null)
    val deformedBody = _deformedBody.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    private val _showWarningMessage = MutableStateFlow(false)
    val showWarningMessage: StateFlow<Boolean> = _showWarningMessage.asStateFlow()

    private val _isMappingData = MutableStateFlow(false)
    val isMappingData: StateFlow<Boolean> = _isMappingData.asStateFlow()

    private val _isLoadingData = MutableStateFlow(false)
    val isLoadingData = _isLoadingData.asStateFlow()

    private val _simulationRecord = MutableStateFlow(SimulationRecord())
    val simulationRecord = _simulationRecord.asStateFlow()

    private val _isSavingData = MutableStateFlow(false)
    val isSavingData = _isSavingData.asStateFlow()

    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges = _hasUnsavedChanges.asStateFlow()

    fun handleSimulationRecordId(simulationRecordId: Int) {

        viewModelScope.launch {
            loadSimulationRecord(simulationRecordId)

            mapDataFromSimulationRecord()

            saveSimulationRecord()
        }
    }

    fun saveSimulationRecord() {

        _isSavingData.value = true

        val (sizeX, sizeY, sizeZ) = _bodySize.value

        val (splitX, splitY, splitZ) = _bodySplits.value

        val now = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())

        _simulationRecord.value = _simulationRecord.value.copy(
            sizeX = sizeX,
            sizeY = sizeY,
            sizeZ = sizeZ,
            splitX = splitX.toInt(),
            splitY = splitY.toInt(),
            splitZ = splitZ.toInt(),
            youngModule = _youngModule.value.toDouble(),
            poissonRatio = _poissonRatio.value.toDouble(),
            pressure = _pressure.value.toDouble(),
            fixedFaces = femBody.fixedFaces.toList(),
            loadedFaces = femBody.loadedFaces.toList(),
            dateModified = now,
            isBodyDeformed = _deformedBody.value != null
        )

        viewModelScope.launch {
            updateSimulationRecordUseCase(_simulationRecord.value)

            savePreferenceUseCase(_simulationRecord.value.id)

            _isSavingData.value = false
        }
    }

    private suspend fun loadSimulationRecord(simulationRecordId: Int) {
        _isLoadingData.value = true

        val record = withContext(Dispatchers.IO) {
            getSimulationRecordUseCase(simulationRecordId)
        }

        if (record != null) {
            _simulationRecord.value = record
        }

        _isLoadingData.value = false
    }

    private fun mapDataFromSimulationRecord() {

        _isMappingData.value = true

        val record = _simulationRecord.value

        val sizes = Vector3(record.sizeX, record.sizeY, record.sizeZ)

        val splits = Vector3(
            record.splitX.toFloat(),
            record.splitY.toFloat(),
            record.splitZ.toFloat()
        )

        _bodySize.value = sizes
        _bodySplits.value = splits

        _youngModule.value = record.youngModule.toString()
        _poissonRatio.value = record.poissonRatio.toString()
        _pressure.value = record.pressure.toString()

        rebuildMesh()

        femBody.fixedFaces.addAll(record.fixedFaces)
        femBody.loadedFaces.addAll(record.loadedFaces)

        femBody.updateBoundaryArrays(_pressure.value.toDoubleOrNull() ?: 0.0)

        _isMappingData.value = false

        _hasUnsavedChanges.value = false

        if (record.isBodyDeformed) {
            calculate()
        }
    }

    fun updateSize(newSize: Vector3) {
        _bodySize.value = newSize
        _hasUnsavedChanges.value = true
        rebuildMesh()
    }

    fun updateSplits(newSplits: Vector3) {
        _bodySplits.value = newSplits
        _hasUnsavedChanges.value = true
        rebuildMesh()
    }

    fun updateYoungModule(value: String) {
        _hasUnsavedChanges.value = true
        _youngModule.value = value
    }

    fun updatePoissonRatio(value: String) {
        _hasUnsavedChanges.value = true
        _poissonRatio.value = value
    }

    fun updatePressure(value: String) {

        _hasUnsavedChanges.value = true

        _pressure.value = value

        femBody.updateBoundaryArrays(value.toDoubleOrNull() ?: 0.0)
    }

    private fun rebuildMesh() {
        femBody.buildElements(_bodySize.value, _bodySplits.value)

        _deformedBody.value = null
    }

    fun toggleFaceFixed(face: ElementFace) {
        if (femBody.fixedFaces.contains(face)) {
            femBody.fixedFaces.remove(face)
        } else {
            femBody.fixedFaces.add(face)
            femBody.loadedFaces.remove(face)
        }

        _hasUnsavedChanges.value = true

        femBody.updateBoundaryArrays(_pressure.value.toDoubleOrNull() ?: 0.0)
    }

    fun toggleFaceLoaded(face: ElementFace) {
        if (femBody.loadedFaces.contains(face)) {
            femBody.loadedFaces.remove(face)
        } else {
            femBody.loadedFaces.add(face)
            femBody.fixedFaces.remove(face)
        }

        _hasUnsavedChanges.value = true

        femBody.updateBoundaryArrays(_pressure.value.toDoubleOrNull() ?: 0.0)
    }

    fun calculate() {
        if (femBody.fixedFaces.isEmpty() || femBody.loadedFaces.isEmpty()) {
            _showWarningMessage.value = true
            return
        }

        val e = _youngModule.value.toDoubleOrNull() ?: return
        val v = _poissonRatio.value.toDoubleOrNull() ?: return
        val p = _pressure.value.toDoubleOrNull() ?: return

        viewModelScope.launch {
            _isCalculating.value = true

            val result = withContext(Dispatchers.Default) {
                femBody.solve(e, v, p)
            }

            _deformedBody.value = result

            _hasUnsavedChanges.value = true

            _isCalculating.value = false
        }
    }

    fun hideWarningMessage() {
        _showWarningMessage.value = false
    }
}