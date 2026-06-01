package com.rostykstets.femlab.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rostykstets.femlab.domain.ElementFace
import com.rostykstets.femlab.domain.FEMBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FEMViewModel(val femBody: FEMBody): ViewModel() {

    private val _bodySize = MutableStateFlow(Vector3(4f, 4f, 4f))
    val bodySize = _bodySize.asStateFlow()

    private val _bodySplits = MutableStateFlow(Vector3(2f, 1f, 2f))
    val bodySplits = _bodySplits.asStateFlow()

    private val _youngModule = MutableStateFlow("4")
    val youngModule = _youngModule.asStateFlow()

    private val _poissonRatio = MutableStateFlow("0.3") // v (зазвичай ~0.3)
    val poissonRatio = _poissonRatio.asStateFlow()

    private val _pressure = MutableStateFlow("2")
    val pressure = _pressure.asStateFlow()

    // --- Стани обчислень ---
    private val _deformedBody = MutableStateFlow<DoubleArray?>(null)
    val deformedBody = _deformedBody.asStateFlow()

    // Стан завантаження
    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating

    init {
        // Будуємо початкову сітку при створенні ViewModel
        rebuildMesh()
    }

    // --- Методи для оновлення станів з UI ---
    fun updateSize(newSize: Vector3) {
        _bodySize.value = newSize
        rebuildMesh()
    }

    fun updateSplits(newSplits: Vector3) {
        _bodySplits.value = newSplits
        rebuildMesh()
    }

    fun updateYoungModule(value: String) { _youngModule.value = value }
    fun updatePoissonRatio(value: String) { _poissonRatio.value = value }
    fun updatePressure(value: String) {
        _pressure.value = value
        // Одразу оновлюємо масиви граничних умов у FEMBody
        femBody.updateBoundaryArrays(value.toDoubleOrNull() ?: 0.0)
    }

    // --- Основні дії ---
    private fun rebuildMesh() {
        femBody.buildElements(_bodySize.value, _bodySplits.value)
        // Скидаємо попередній розв'язок
        _deformedBody.value = null
    }

    fun toggleFaceFixed(face: ElementFace) {
        if (femBody.fixedFaces.contains(face)) {
            femBody.fixedFaces.remove(face)
        } else {
            femBody.fixedFaces.add(face)
            femBody.loadedFaces.remove(face)
        }
        femBody.updateBoundaryArrays(_pressure.value.toDoubleOrNull() ?: 0.0)
    }

    fun toggleFaceLoaded(face: ElementFace) {
        if (femBody.loadedFaces.contains(face)) {
            femBody.loadedFaces.remove(face)
        } else {
            femBody.loadedFaces.add(face)
            femBody.fixedFaces.remove(face)
        }
        femBody.updateBoundaryArrays(_pressure.value.toDoubleOrNull() ?: 0.0)
    }

    fun calculate() {
        val e = _youngModule.value.toDoubleOrNull() ?: return
        val v = _poissonRatio.value.toDoubleOrNull() ?: return
        val p = _pressure.value.toDoubleOrNull() ?: return

        viewModelScope.launch {
            _isCalculating.value = true

            // Важка математика на фоновому потоці
            val result = withContext(Dispatchers.Default) {
                femBody.solve(e, v, p)
            }

            _deformedBody.value = result
            _isCalculating.value = false
        }
    }
}