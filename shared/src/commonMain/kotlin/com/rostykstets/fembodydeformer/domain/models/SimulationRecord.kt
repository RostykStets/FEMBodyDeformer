package com.rostykstets.fembodydeformer.domain.models

import kotlinx.datetime.LocalDateTime


data class SimulationRecord(
    val id: Int,
    val title: String,
    val dateAdded: LocalDateTime,
    val dateModified: LocalDateTime,
    val sizeX: Float,
    val sizeY: Float,
    val sizeZ: Float,
    val splitX: Int,
    val splitY: Int,
    val splitZ: Int,
    val youngModule: Double,
    val poissonRatio: Double,
    val pressure: Double,
    val fixedFaces: List<ElementFace>,
    val loadedFaces: List<ElementFace>,
    val isBodyDeformed: Boolean
)
