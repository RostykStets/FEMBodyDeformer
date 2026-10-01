package com.rostykstets.fembodydeformer.domain.models

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class SimulationRecord(
    val id: Int = 0,
    val title: String = "",
    val dateAdded: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault()),
    val dateModified: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault()),
    val sizeX: Float = 4f,
    val sizeY: Float = 4f,
    val sizeZ: Float = 4f,
    val splitX: Int = 2,
    val splitY: Int = 1,
    val splitZ: Int = 2,
    val youngModule: Double = 4.0,
    val poissonRatio: Double = 0.3,
    val pressure: Double = 2.0,
    val fixedFaces: List<ElementFace> = emptyList(),
    val loadedFaces: List<ElementFace> = emptyList(),
    val isBodyDeformed: Boolean = false
)